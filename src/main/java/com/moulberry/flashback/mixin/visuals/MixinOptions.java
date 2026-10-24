package com.moulberry.flashback.mixin.visuals;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.moulberry.flashback.state.EditorState;
import com.moulberry.flashback.state.EditorStateManager;
import com.moulberry.flashback.ext.OptionsExt;
import net.minecraft.client.CloudStatus;
import net.minecraft.client.OptionInstance;
import net.minecraft.client.Options;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(Options.class)
public class MixinOptions implements OptionsExt {

    @Shadow
    @Final
    private OptionInstance<Integer> fov;
    @Unique
    private OptionInstance<Integer> cachedFovOptionInstance = null;

    @Inject(method = "fov", at = @At("RETURN"), cancellable = true)
    public void fov(CallbackInfoReturnable<OptionInstance<Integer>> cir) {
        EditorState editorState = EditorStateManager.getCurrent();
        if (editorState != null && editorState.replayVisuals.overrideFov) {
            if (this.cachedFovOptionInstance == null || this.cachedFovOptionInstance.get() != Math.round(editorState.replayVisuals.overrideFovAmount)) {
                OptionInstance<Integer> delegate = cir.getReturnValue();
                this.cachedFovOptionInstance = new OptionInstance<>("options.fov", OptionInstance.noTooltip(), Options::genericValueLabel,
                    delegate.values(), delegate.codec(), Math.round(editorState.replayVisuals.overrideFovAmount), value -> {
                    editorState.replayVisuals.overrideFov = false;
                    delegate.set(value);
                });
            }

            cir.setReturnValue(this.cachedFovOptionInstance);
        }
    }

    @WrapOperation(method = "extractRenderState", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/Options;getCloudStatus()Lnet/minecraft/client/CloudStatus;"), require = 0)
    public CloudStatus extractRenderState_getCloudStatus(Options instance, Operation<CloudStatus> original) {
        EditorState editorState = EditorStateManager.getCurrent();
        if (editorState != null && !editorState.replayVisuals.renderSky) {
            return CloudStatus.OFF;
        } else {
            return original.call(instance);
        }
    }

    @Override
    public int flashback$getOriginalFov() {
        return this.fov.get();
    }
}
