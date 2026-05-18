package com.datrat.tiab.entity;

import com.datrat.tiab.config.NBTKeys;
import com.datrat.tiab.config.TiabConfig;
import cpw.mods.fml.common.registry.IEntityAdditionalSpawnData;
import io.netty.buffer.ByteBuf;
import net.minecraft.block.Block;
import net.minecraft.entity.Entity;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;

public class TimeAcceleratorEntity extends Entity implements IEntityAdditionalSpawnData {
    private static final int TIME_RATE_WATCHER = 16;

    private int remainingTime;
    private int targetX;
    private int targetY;
    private int targetZ;
    private boolean hasTarget;

    public TimeAcceleratorEntity(World world) {
        super(world);
        setSize(1.0F, 1.0F);
        noClip = true;
    }

    public TimeAcceleratorEntity(World world, int x, int y, int z) {
        this(world);
        setTarget(x, y, z);
        setPosition(x + 0.5D, y + 0.5D, z + 0.5D);
    }

    @Override
    protected void entityInit() {
        dataWatcher.addObject(TIME_RATE_WATCHER, Integer.valueOf(1));
    }

    @Override
    public void onUpdate() {
        super.onUpdate();

        if (!hasTarget) {
            if (!worldObj.isRemote) {
                setDead();
            }
            return;
        }

        if (!worldObj.isRemote) {
            tickTarget();
            remainingTime--;
            if (remainingTime <= 0) {
                setDead();
            }
        }
    }

    private void tickTarget() {
        TileEntity tile = worldObj.getTileEntity(targetX, targetY, targetZ);
        Block block = worldObj.getBlock(targetX, targetY, targetZ);

        for (int i = 0; i < getTimeRate(); i++) {
            if (tile != null && !tile.isInvalid()) {
                tile.updateEntity();
            } else if (block != null && block.getTickRandomly()) {
                if (worldObj.rand.nextInt(TiabConfig.averageUpdateRandomTick) == 0) {
                    block.updateTick(worldObj, targetX, targetY, targetZ, worldObj.rand);
                }
            } else {
                setDead();
                return;
            }
        }
    }

    @Override
    protected void readEntityFromNBT(NBTTagCompound compound) {
        setTimeRate(compound.getInteger(NBTKeys.ENTITY_TIME_RATE));
        remainingTime = compound.getInteger(NBTKeys.ENTITY_REMAINING_TIME);
        if (compound.hasKey(NBTKeys.ENTITY_POS)) {
            NBTTagCompound pos = compound.getCompoundTag(NBTKeys.ENTITY_POS);
            setTarget(pos.getInteger("x"), pos.getInteger("y"), pos.getInteger("z"));
        }
    }

    @Override
    protected void writeEntityToNBT(NBTTagCompound compound) {
        compound.setInteger(NBTKeys.ENTITY_TIME_RATE, getTimeRate());
        compound.setInteger(NBTKeys.ENTITY_REMAINING_TIME, remainingTime);
        NBTTagCompound pos = new NBTTagCompound();
        pos.setInteger("x", targetX);
        pos.setInteger("y", targetY);
        pos.setInteger("z", targetZ);
        compound.setTag(NBTKeys.ENTITY_POS, pos);
    }

    @Override
    public void writeSpawnData(ByteBuf buffer) {
        buffer.writeInt(targetX);
        buffer.writeInt(targetY);
        buffer.writeInt(targetZ);
        buffer.writeInt(remainingTime);
        buffer.writeInt(getTimeRate());
        buffer.writeBoolean(hasTarget);
    }

    @Override
    public void readSpawnData(ByteBuf buffer) {
        int x = buffer.readInt();
        int y = buffer.readInt();
        int z = buffer.readInt();
        remainingTime = buffer.readInt();
        setTimeRate(buffer.readInt());
        if (buffer.readBoolean()) {
            setTarget(x, y, z);
            setPosition(x + 0.5D, y + 0.5D, z + 0.5D);
        }
    }

    private void setTarget(int x, int y, int z) {
        this.targetX = x;
        this.targetY = y;
        this.targetZ = z;
        this.hasTarget = true;
    }

    public int getTimeRate() {
        return dataWatcher.getWatchableObjectInt(TIME_RATE_WATCHER);
    }

    public void setTimeRate(int rate) {
        dataWatcher.updateObject(TIME_RATE_WATCHER, Integer.valueOf(rate));
    }

    public int getRemainingTime() {
        return remainingTime;
    }

    public void setRemainingTime(int remainingTime) {
        this.remainingTime = remainingTime;
    }
}
