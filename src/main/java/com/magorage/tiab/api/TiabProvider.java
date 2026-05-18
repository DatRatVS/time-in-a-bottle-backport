package com.magorage.tiab.api;

import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.common.Loader;
import cpw.mods.fml.common.ModContainer;
import cpw.mods.fml.common.event.FMLInterModComms;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Consumer;

public final class TiabProvider {
    private static final Map<String, TiabProvider> PENDING = new HashMap<String, TiabProvider>();

    private final String modId;
    private final Consumer<ITimeInABottleAPI> apiConsumer;
    private final TiabAPIHooks hooks;
    private boolean received;

    public TiabProvider(Consumer<ITimeInABottleAPI> apiConsumer) {
        this(apiConsumer, TiabAPIHooks.create().build());
    }

    public TiabProvider(Consumer<ITimeInABottleAPI> apiConsumer, TiabAPIHooks hooks) {
        this.modId = activeModId();
        this.apiConsumer = apiConsumer;
        this.hooks = hooks;
    }

    public void request() {
        requestApi(this);
    }

    public static void requestApi(TiabProvider provider) {
        PENDING.put(provider.getModID(), provider);
        FMLInterModComms.sendRuntimeMessage(provider.getModID(), ITimeInABottleAPI.IMC.MOD_ID, ITimeInABottleAPI.IMC.GET_API, provider.getModID());
    }

    public static TiabProvider takePending(String modId) {
        return PENDING.remove(modId);
    }

    public String getModID() {
        return modId;
    }

    public TiabAPIHooks getHooks() {
        return hooks;
    }

    public void setAPI(ITimeInABottleAPI api) {
        if (!received) {
            apiConsumer.accept(api);
        }
    }

    public void setFinalized() {
        this.received = true;
    }

    private static String activeModId() {
        ModContainer container = Loader.instance().activeModContainer();
        if (container != null) {
            return container.getModId();
        }
        ModContainer source = FMLCommonHandler.instance().findContainerFor(TiabProvider.class);
        return source != null ? source.getModId() : "unknown";
    }
}
