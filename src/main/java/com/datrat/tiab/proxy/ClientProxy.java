package com.datrat.tiab.proxy;

import com.datrat.tiab.client.TimeAcceleratorRenderer;
import com.datrat.tiab.entity.TimeAcceleratorEntity;
import cpw.mods.fml.client.registry.RenderingRegistry;

public class ClientProxy extends CommonProxy {
    @Override
    public void registerRenderers() {
        RenderingRegistry.registerEntityRenderingHandler(TimeAcceleratorEntity.class, new TimeAcceleratorRenderer());
    }
}
