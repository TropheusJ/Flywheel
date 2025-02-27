package com.jozufozu.flywheel.vanilla;

import com.jozufozu.flywheel.backend.instancing.IDynamicInstance;
import com.jozufozu.flywheel.backend.instancing.tile.TileEntityInstance;
import com.jozufozu.flywheel.backend.material.MaterialManager;
import com.jozufozu.flywheel.core.Materials;
import com.jozufozu.flywheel.core.materials.OrientedData;
import com.jozufozu.flywheel.core.model.ModelPart;
import com.jozufozu.flywheel.util.AnimationTickHolder;

import net.minecraft.client.renderer.tileentity.BellTileEntityRenderer;
import net.minecraft.tileentity.BellTileEntity;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.vector.Quaternion;
import net.minecraft.util.math.vector.Vector3f;

public class BellInstance extends TileEntityInstance<BellTileEntity> implements IDynamicInstance {

	private final OrientedData bell;

	private float lastRingTime = Float.NaN;

	public BellInstance(MaterialManager<?> materialManager, BellTileEntity tile) {
		super(materialManager, tile);

		bell = createBellInstance()
				.setPivot(0.5f, 0.75f, 0.5f)
				.setPosition(getInstancePosition());
	}

	@Override
	public void beginFrame() {
		float ringTime = (float)tile.ticks + AnimationTickHolder.getPartialTicks();

		if (ringTime == lastRingTime) return;
		lastRingTime = ringTime;

		if (tile.shaking) {
			float angle = MathHelper.sin(ringTime / (float) Math.PI) / (4.0F + ringTime / 3.0F);

			Vector3f ringAxis = tile.clickDirection.getCounterClockWise().step();

			bell.setRotation(ringAxis.rotation(angle));
		} else {
			bell.setRotation(Quaternion.ONE);
		}
	}

	@Override
	public void updateLight() {
		relight(getWorldPosition(), bell);
	}

	@Override
	public void remove() {
		bell.delete();
	}

	private OrientedData createBellInstance() {
        return materialManager.defaultCutout()
                .material(Materials.ORIENTED)
				.model(tile.getType(), BellInstance::createBellModel)
				.createInstance();
	}

	private static ModelPart createBellModel() {
		return ModelPart.builder(32, 32)
				.sprite(BellTileEntityRenderer.BELL_RESOURCE_LOCATION.sprite())
				.cuboid()
				.start(5.0F, 6.0F, 5.0F)
				.size(6.0F, 7.0F, 6.0F)
				.endCuboid()
				.cuboid()
				.textureOffset(0, 13)
				.start(4.0F, 4.0F, 4.0F)
				.size(8.0F, 2.0F, 8.0F)
				.endCuboid()
				.build();
	}
}
