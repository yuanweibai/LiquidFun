/**
 * Copyright (c) 2014 Google, Inc. All rights reserved.
 * <p>
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * <p>
 * http://www.apache.org/licenses/LICENSE-2.0
 * <p>
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.google.fpl.liquidfunpaint.tool;

import com.google.fpl.liquidfun.ParticleFlag;
import com.google.fpl.liquidfun.ParticleGroup;
import com.google.fpl.liquidfun.ParticleGroupDef;
import com.google.fpl.liquidfun.ParticleGroupFlag;
import com.google.fpl.liquidfun.ParticleSystem;
import com.google.fpl.liquidfunpaint.Renderer;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;

/**
 * Water tool
 * We create particle groups per draw, but we don't need to join them.
 * Particle groups are merely used to mimic the shape of a stroke.
 */
public class WaterTool extends Tool {
    private ParticleGroup mParticleGroup = null;

    public WaterTool() {
        super(ToolType.WATER);
        mParticleFlags = ParticleFlag.waterParticle | ParticleFlag.colorMixingParticle;
        mParticleGroupFlags = ParticleGroupFlag.particleGroupCanBeEmpty;
    }

    /**
     * @param pInfo The pointer info containing the previous group info
     */
    @Override
    protected void applyTool(PointerInfo pInfo) {
        // If we have a ParticleGroup saved already, assign it to pInfo.
        // If not, we take the first ParticleGroup created for wall particles,
        // which will be contained in pInfo.
        if (mParticleGroup != null) {
            pInfo.setParticleGroup(mParticleGroup);
        } else if (pInfo.getParticleGroup() != null) {
            mParticleGroup = pInfo.getParticleGroup();
        }

        super.applyTool(pInfo);
    }

    @Override
    protected void reset() {
        mParticleGroup = null;
    }

    public void init() {
        for (int i = 0;i<20;i++){
            ByteBuffer buffer = ByteBuffer
                    .allocateDirect(40 * 2 * 40)
                    .order(ByteOrder.nativeOrder());
            ParticleGroupDef pgd = new ParticleGroupDef();
            pgd.setFlags(mParticleFlags);
            pgd.setGroupFlags(mParticleGroupFlags);
            pgd.setLinearVelocity(mVelocity);
            pgd.setColor(mColor);
            pgd.setCircleShapesFromVertexList(buffer.slice(), 5, 0.09f);
            ParticleSystem ps = Renderer.getInstance().acquireParticleSystem();
            try {
                ps.createParticleGroup(pgd);
                pgd.delete();
            } finally {
                Renderer.getInstance().releaseParticleSystem();
            }
        }
    }
}
