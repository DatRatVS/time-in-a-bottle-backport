package com.datrat.tiab.item;

import com.datrat.tiab.TimeInABottle;
import com.datrat.tiab.api.InternalApi;
import com.datrat.tiab.config.NBTKeys;
import com.datrat.tiab.config.TiabConfig;
import com.datrat.tiab.entity.TimeAcceleratorEntity;
import com.datrat.tiab.util.Chat;
import com.datrat.tiab.util.TimeFormat;
import com.magorage.tiab.api.ITimeInABottleAPI;
import net.minecraft.block.Block;
import net.minecraft.client.resources.I18n;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.world.World;

import java.util.List;

public class TimeInABottleItem extends Item {
    private static final String[] NOTE_NAMES = {"harp", "harp", "harp", "harp", "harp", "harp", "harp", "harp", "harp", "harp", "harp"};
    private static final float[] NOTE_PITCHES = {
            0.5F, 0.561231F, 0.629961F, 0.667420F, 0.749154F,
            0.840896F, 0.943874F, 1.0F, 1.122462F, 1.259921F, 1.334840F
    };

    public TimeInABottleItem() {
        setUnlocalizedName(TimeInABottle.MOD_ID + ".time_in_a_bottle");
        setTextureName(TimeInABottle.MOD_ID + ":time_in_a_bottle");
        setCreativeTab(CreativeTabs.tabTools);
        setMaxStackSize(1);
    }

    @Override
    public void onUpdate(ItemStack stack, World world, Entity entity, int slot, boolean selected) {
        super.onUpdate(stack, world, entity, slot, selected);
        if (world.isRemote || !(entity instanceof EntityPlayer)) {
            return;
        }

        ITimeInABottleAPI api = InternalApi.getInternalApi();
        EntityPlayerMP player = entity instanceof EntityPlayerMP ? (EntityPlayerMP) entity : null;

        if (!api.canUse()) {
            api.callTickEvent(player, stack);
            return;
        }

        if (world.getTotalWorldTime() % 20L == 0L) {
            setStoredTime(stack, getStoredTime(stack) + 20);
            setTotalTime(stack, getTotalTime(stack) + 20);
        }

        if (world.getTotalWorldTime() % 200L == 0L) {
            clearLowerDuplicateBottles((EntityPlayer) entity, stack);
        }
    }

    private void clearLowerDuplicateBottles(EntityPlayer player, ItemStack currentStack) {
        int myTime = getStoredTime(currentStack);
        for (int i = 0; i < player.inventory.getSizeInventory(); i++) {
            ItemStack invStack = player.inventory.getStackInSlot(i);
            if (invStack != null && invStack.getItem() == this && invStack != currentStack) {
                int otherTime = getStoredTime(invStack);
                if (myTime < otherTime) {
                    setStoredTime(currentStack, 0);
                    return;
                }
            }
        }
    }

    @Override
    public boolean onItemUse(ItemStack stack, EntityPlayer player, World world, int x, int y, int z,
                             int side, float hitX, float hitY, float hitZ) {
        if (world.isRemote) {
            return true;
        }

        Block block = world.getBlock(x, y, z);
        if (world.getTileEntity(x, y, z) == null && !block.getTickRandomly()) {
            return false;
        }

        ITimeInABottleAPI api = InternalApi.getInternalApi();
        if (api.canUse()) {
            return accelerateBlock(api, stack, player, world, x, y, z);
        }

        if (!api.callUseEvent(stack, player, world, x, y, z)) {
            Chat.send(player, "TIAB has had its API access revoked.");
        }
        return true;
    }

    public boolean accelerateBlock(ITimeInABottleAPI api, ItemStack stack, EntityPlayer player, World world, int x, int y, int z) {
        int nextRate = 1;
        int energyRequired = getEnergyCost(nextRate);
        boolean creative = player != null && player.capabilities.isCreativeMode;
        TimeAcceleratorEntity accelerator = findAccelerator(world, x, y, z);

        if (accelerator != null) {
            int currentRate = accelerator.getTimeRate();
            int maxRate = 1 << Math.max(0, TiabConfig.maxTimeRatePower - 1);
            int usedUpTime = getEachUseDuration() - accelerator.getRemainingTime();

            if (currentRate >= maxRate) {
                return true;
            }

            nextRate = currentRate * 2;
            energyRequired = getEnergyCost(nextRate);

            if (!canUse(stack, creative, energyRequired)) {
                return true;
            }

            accelerator.setTimeRate(nextRate);
            accelerator.setRemainingTime(accelerator.getRemainingTime() + usedUpTime / 2);
        } else {
            if (!canUse(stack, creative, energyRequired)) {
                return true;
            }

            accelerator = new TimeAcceleratorEntity(world, x, y, z);
            accelerator.setRemainingTime(getEachUseDuration());
            world.spawnEntityInWorld(accelerator);
        }

        if (!creative) {
            applyDamage(stack, energyRequired);
        }
        playSound(world, x, y, z, nextRate);
        return true;
    }

    private TimeAcceleratorEntity findAccelerator(World world, int x, int y, int z) {
        AxisAlignedBB box = AxisAlignedBB.getBoundingBox(x, y, z, x + 1.0D, y + 1.0D, z + 1.0D);
        List entities = world.getEntitiesWithinAABB(TimeAcceleratorEntity.class, box);
        return entities.isEmpty() ? null : (TimeAcceleratorEntity) entities.get(0);
    }

    public int getEachUseDuration() {
        return 20 * TiabConfig.eachUseDuration;
    }

    public int getEnergyCost(int timeRate) {
        if (timeRate <= 1) {
            return getEachUseDuration();
        }
        return timeRate / 2 * getEachUseDuration();
    }

    public boolean canUse(ItemStack stack, boolean creative, int energyRequired) {
        return creative || getStoredTime(stack) >= energyRequired;
    }

    public void applyDamage(ItemStack stack, int damage) {
        setStoredTime(stack, getStoredTime(stack) - damage);
    }

    public void playSound(World world, int x, int y, int z, int nextRate) {
        int index = 0;
        int rate = nextRate;
        while (rate > 1 && index < NOTE_PITCHES.length - 1) {
            rate >>= 1;
            index++;
        }
        world.playSoundEffect(x + 0.5D, y + 0.5D, z + 0.5D, "note." + NOTE_NAMES[index], 1.0F, NOTE_PITCHES[index]);
    }

    @Override
    public void addInformation(ItemStack stack, EntityPlayer player, List tooltip, boolean advanced) {
        tooltip.add(I18n.format("item.tiab.time_in_a_bottle.tooltip.stored_time", splitTime(getStoredTime(stack))));
        tooltip.add(I18n.format("item.tiab.time_in_a_bottle.tooltip.total_accumulated_time", splitTime(getTotalTime(stack))));
    }

    private Object[] splitTime(int ticks) {
        String[] parts = TimeFormat.formatTicks(ticks).split(":");
        return new Object[]{parts[0], parts[1], parts[2]};
    }

    public int getStoredTime(ItemStack stack) {
        return getTag(stack).getInteger(NBTKeys.STORED_TIME);
    }

    public void setStoredTime(ItemStack stack, int value) {
        getTag(stack).setInteger(NBTKeys.STORED_TIME, clamp(value));
    }

    public int getTotalTime(ItemStack stack) {
        return getTag(stack).getInteger(NBTKeys.TOTAL_ACCUMULATED_TIME);
    }

    public void setTotalTime(ItemStack stack, int value) {
        getTag(stack).setInteger(NBTKeys.TOTAL_ACCUMULATED_TIME, clamp(value));
    }

    private int clamp(int value) {
        if (value < 0) {
            return 0;
        }
        return Math.min(value, TiabConfig.maxStoredTime);
    }

    private NBTTagCompound getTag(ItemStack stack) {
        if (stack.stackTagCompound == null) {
            stack.stackTagCompound = new NBTTagCompound();
        }
        return stack.stackTagCompound;
    }
}
