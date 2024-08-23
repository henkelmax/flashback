package com.moulberry.flashback.keyframe.impl;

import com.google.gson.*;
import com.moulberry.flashback.Interpolation;
import com.moulberry.flashback.keyframe.Keyframe;
import com.moulberry.flashback.keyframe.handler.KeyframeHandler;
import com.moulberry.flashback.keyframe.interpolation.InterpolationType;
import com.moulberry.flashback.spline.CatmullRom;
import imgui.ImGui;

import java.lang.reflect.Type;
import java.util.function.Consumer;

public class TickrateKeyframe extends Keyframe {

    private float tickrate;

    public TickrateKeyframe(float tickrate) {
        this(tickrate, InterpolationType.DEFAULT);
    }

    public TickrateKeyframe(float tickrate, InterpolationType interpolationType) {
        this.tickrate = tickrate;
        this.interpolationType(interpolationType);
    }

    @Override
    public Keyframe copy() {
        return new TickrateKeyframe(this.tickrate, this.interpolationType());
    }

    @Override
    public void renderEditKeyframe(Consumer<Consumer<Keyframe>> update) {
        ImGui.setNextItemWidth(160);
        float[] input = new float[]{this.tickrate/20f};
        if (ImGui.sliderFloat("Speed", input, 0.1f, 10.0f)) {
            float tickrate = input[0]*20f;
            if (this.tickrate != tickrate) {
                update.accept(keyframe -> ((TickrateKeyframe)keyframe).tickrate = tickrate);
            }
        }
    }

    @Override
    public void apply(KeyframeHandler keyframeHandler) {
        keyframeHandler.applyTickrate(this.tickrate);
    }

    @Override
    public void applyInterpolated(KeyframeHandler keyframeHandler, Keyframe otherGeneric, float amount) {
        if (!(otherGeneric instanceof TickrateKeyframe other)) {
            this.apply(keyframeHandler);
            return;
        }

        float tickrate = Interpolation.linear(this.tickrate, other.tickrate, amount);
        keyframeHandler.applyTickrate(tickrate);
    }

    @Override
    public void applyInterpolatedSmooth(KeyframeHandler keyframeHandler, Keyframe p1, Keyframe p2, Keyframe p3, float t0, float t1, float t2, float t3, float amount, float lerpAmount, boolean lerpFromRight) {
        float time1 = t1 - t0;
        float time2 = t2 - t0;
        float time3 = t3 - t0;

        float tickrate = CatmullRom.value(this.tickrate,
            ((TickrateKeyframe)p1).tickrate, ((TickrateKeyframe)p2).tickrate,
            ((TickrateKeyframe)p3).tickrate, time1, time2, time3, amount);

        if (lerpAmount >= 0) {
            float linearTickrate = Interpolation.linear(((TickrateKeyframe)p1).tickrate, ((TickrateKeyframe)p2).tickrate, lerpAmount);

            if (lerpFromRight) {
                tickrate = Interpolation.linear(tickrate, linearTickrate, amount);
            } else {
                tickrate = Interpolation.linear(linearTickrate, tickrate, amount);
            }
        }

        keyframeHandler.applyTickrate(tickrate);
    }

    public static class TypeAdapter implements JsonSerializer<TickrateKeyframe>, JsonDeserializer<TickrateKeyframe> {
        @Override
        public TickrateKeyframe deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
            JsonObject jsonObject = json.getAsJsonObject();
            float tickrate = jsonObject.get("tickrate").getAsFloat();
            InterpolationType interpolationType = context.deserialize(jsonObject.get("interpolation_type"), InterpolationType.class);
            return new TickrateKeyframe(tickrate, interpolationType);
        }

        @Override
        public JsonElement serialize(TickrateKeyframe src, Type typeOfSrc, JsonSerializationContext context) {
            JsonObject jsonObject = new JsonObject();
            jsonObject.addProperty("tickrate", src.tickrate);
            jsonObject.addProperty("type", "tickrate");
            jsonObject.add("interpolation_type", context.serialize(src.interpolationType()));
            return jsonObject;
        }
    }
}