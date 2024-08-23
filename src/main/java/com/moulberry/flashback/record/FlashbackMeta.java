package com.moulberry.flashback.record;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import org.jetbrains.annotations.Nullable;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.UUID;

public class FlashbackMeta {

    public UUID replayIdentifier = UUID.randomUUID();
    public String name = "Unnamed";
    public String versionString = null;
    public int dataVersion = 0;
    public int protocolVersion = 0;

    public int totalTicks = -1;
    public LinkedHashMap<String, FlashbackChunkMeta> chunks = new LinkedHashMap<>();

    public JsonObject toJson() {
        JsonObject meta = new JsonObject();
        meta.addProperty("uuid", this.replayIdentifier.toString());
        meta.addProperty("name", this.name);

        if (this.versionString != null) {
            meta.addProperty("version_string", this.versionString);
        }
        if (this.dataVersion != 0) {
            meta.addProperty("data_version", this.dataVersion);
        }
        if (this.protocolVersion != 0) {
            meta.addProperty("protocol_version", this.protocolVersion);
        }

        if (this.totalTicks > 0) {
            meta.addProperty("total_ticks", this.totalTicks);
        }

        JsonObject chunksJson = new JsonObject();
        for (Map.Entry<String, FlashbackChunkMeta> entry : this.chunks.entrySet()) {
            chunksJson.add(entry.getKey(), entry.getValue().toJson());
        }
        meta.add("chunks", chunksJson);

        return meta;
    }

    @Nullable
    public static FlashbackMeta fromJson(JsonObject meta) {
        FlashbackMeta flashbackMeta = new FlashbackMeta();

        // UUID
        if (!meta.has("uuid")) {
            return null;
        }
        flashbackMeta.replayIdentifier = UUID.fromString(meta.get("uuid").getAsString());

        // Name
        if (!meta.has("name")) {
            return null;
        }
        flashbackMeta.name = meta.get("name").getAsString();

        if (meta.has("version_string")) {
            flashbackMeta.versionString = meta.get("version_string").getAsString();
        }
        if (meta.has("data_version")) {
            flashbackMeta.dataVersion = meta.get("data_version").getAsInt();
        }
        if (meta.has("protocol_version")) {
            flashbackMeta.protocolVersion = meta.get("protocol_version").getAsInt();
        }

        // Total ticks
        if (meta.has("total_ticks")) {
            flashbackMeta.totalTicks = meta.get("total_ticks").getAsInt();
        }

        // Chunks
        if (!meta.has("chunks")) {
            return null;
        }
        JsonObject chunksJson = meta.getAsJsonObject("chunks");
        for (Map.Entry<String, JsonElement> entry : chunksJson.entrySet()) {
            FlashbackChunkMeta chunkMeta = FlashbackChunkMeta.fromJson(entry.getValue().getAsJsonObject());
            if (chunkMeta == null) {
                return null;
            }
            flashbackMeta.chunks.put(entry.getKey(), chunkMeta);
        }

        return flashbackMeta;
    }

}