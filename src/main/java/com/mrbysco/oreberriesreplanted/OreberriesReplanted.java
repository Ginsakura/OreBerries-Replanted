package com.mrbysco.oreberriesreplanted;

import com.mojang.logging.LogUtils;
import com.mrbysco.oreberriesreplanted.client.ClientHandler;
import com.mrbysco.oreberriesreplanted.config.OreBerriesConfig;
import com.mrbysco.oreberriesreplanted.handler.WorldgenHandler;
import com.mrbysco.oreberriesreplanted.registry.OreBerryPlacementModifiers;
import com.mrbysco.oreberriesreplanted.registry.OreBerryRecipeTypes;
import com.mrbysco.oreberriesreplanted.registry.OreBerryRegistry;
import com.mrbysco.oreberriesreplanted.worldgen.OreBerryFeatures;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.DistExecutor;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.config.ModConfig;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import org.slf4j.Logger;

@Mod(Reference.MOD_ID)
public class OreberriesReplanted {
	public static final Logger LOGGER = LogUtils.getLogger();

	public OreberriesReplanted() {
		IEventBus eventBus = FMLJavaModLoadingContext.get().getModEventBus();
		ModLoadingContext.get().registerConfig(ModConfig.Type.COMMON, OreBerriesConfig.commonSpec);
		eventBus.register(OreBerriesConfig.class);

		eventBus.addListener(this::commonSetup);

		OreBerryRegistry.BLOCKS.register(eventBus);
		OreBerryRegistry.ITEMS.register(eventBus);
		OreBerryRegistry.FLUIDS.register(eventBus);
		OreBerryRegistry.BLOCK_ENTITIES.register(eventBus);
		OreBerryRegistry.RECIPE_SERIALIZERS.register(eventBus);
		OreBerryRegistry.FEATURES.register(eventBus);

		MinecraftForge.EVENT_BUS.register(new WorldgenHandler());

		DistExecutor.unsafeRunWhenOn(Dist.CLIENT, () -> () -> {
			eventBus.addListener(ClientHandler::onClientSetup);
			eventBus.addListener(ClientHandler::registerEntityRenders);
		});
	}

	private void commonSetup(FMLCommonSetupEvent event) {
		OreBerryRecipeTypes.init();
		OreBerryPlacementModifiers.init();
		OreBerryFeatures.init();
		event.enqueueWork(() -> {
			OreBerryRegistry.registerBlockData();
		});
	}
}
