package org.bukkit.craftbukkit.inventory;

import com.google.common.base.Objects;
import com.google.common.collect.ImmutableMap;
import java.util.Map;
import net.minecraft.server.BlockJukeBox;
import net.minecraft.server.NBTBase;
import net.minecraft.server.NBTTagCompound;
import net.minecraft.server.TileEntity;
import net.minecraft.server.TileEntityBanner;
import net.minecraft.server.TileEntityBeacon;
import net.minecraft.server.TileEntityBrewingStand;
import net.minecraft.server.TileEntityChest;
import net.minecraft.server.TileEntityCommand;
import net.minecraft.server.TileEntityComparator;
import net.minecraft.server.TileEntityDispenser;
import net.minecraft.server.TileEntityDropper;
import net.minecraft.server.TileEntityEnchantTable;
import net.minecraft.server.TileEntityEndGateway;
import net.minecraft.server.TileEntityEnderChest;
import net.minecraft.server.TileEntityFlowerPot;
import net.minecraft.server.TileEntityFurnace;
import net.minecraft.server.TileEntityHopper;
import net.minecraft.server.TileEntityLightDetector;
import net.minecraft.server.TileEntityMobSpawner;
import net.minecraft.server.TileEntityNote;
import net.minecraft.server.TileEntityShulkerBox;
import net.minecraft.server.TileEntitySign;
import net.minecraft.server.TileEntitySkull;
import net.minecraft.server.TileEntityStructure;
import org.apache.commons.lang.Validate;
import org.bukkit.Material;
import org.bukkit.block.BlockState;
import org.bukkit.configuration.serialization.DelegateDeserialization;
import org.bukkit.craftbukkit.block.CraftBanner;
import org.bukkit.craftbukkit.block.CraftBeacon;
import org.bukkit.craftbukkit.block.CraftBlockEntityState;
import org.bukkit.craftbukkit.block.CraftBrewingStand;
import org.bukkit.craftbukkit.block.CraftChest;
import org.bukkit.craftbukkit.block.CraftCommandBlock;
import org.bukkit.craftbukkit.block.CraftComparator;
import org.bukkit.craftbukkit.block.CraftCreatureSpawner;
import org.bukkit.craftbukkit.block.CraftDaylightDetector;
import org.bukkit.craftbukkit.block.CraftDispenser;
import org.bukkit.craftbukkit.block.CraftDropper;
import org.bukkit.craftbukkit.block.CraftEnchantingTable;
import org.bukkit.craftbukkit.block.CraftEndGateway;
import org.bukkit.craftbukkit.block.CraftEnderChest;
import org.bukkit.craftbukkit.block.CraftFlowerPot;
import org.bukkit.craftbukkit.block.CraftFurnace;
import org.bukkit.craftbukkit.block.CraftHopper;
import org.bukkit.craftbukkit.block.CraftJukebox;
import org.bukkit.craftbukkit.block.CraftNoteBlock;
import org.bukkit.craftbukkit.block.CraftShulkerBox;
import org.bukkit.craftbukkit.block.CraftSign;
import org.bukkit.craftbukkit.block.CraftSkull;
import org.bukkit.craftbukkit.block.CraftStructureBlock;
import org.bukkit.craftbukkit.util.CraftMagicNumbers;
import org.bukkit.inventory.meta.BlockStateMeta;

@DelegateDeserialization(CraftMetaItem.SerializableMeta.class)
public class CraftMetaBlockState extends CraftMetaItem implements BlockStateMeta {

    @ItemMetaKey.Specific(ItemMetaKey.Specific.To.NBT)
    static final ItemMetaKey BLOCK_ENTITY_TAG = new ItemMetaKey("BlockEntityTag");

    final Material material;
    NBTTagCompound blockEntityTag;

    CraftMetaBlockState(CraftMetaItem meta, Material material) {
        super(meta);
        this.material = material;

        if (!(meta instanceof CraftMetaBlockState)
                || ((CraftMetaBlockState) meta).material != material) {
            blockEntityTag = null;
            return;
        }

        CraftMetaBlockState te = (CraftMetaBlockState) meta;
        this.blockEntityTag = te.blockEntityTag;
    }

    CraftMetaBlockState(NBTTagCompound tag, Material material) {
        super(tag);
        this.material = material;

        if (tag.hasKeyOfType(BLOCK_ENTITY_TAG.NBT, CraftMagicNumbers.NBT.TAG_COMPOUND)) {
            blockEntityTag = tag.getCompound(BLOCK_ENTITY_TAG.NBT);
        } else {
            blockEntityTag = null;
        }
    }

    CraftMetaBlockState(Map<String, Object> map) {
        super(map);
        String matName = SerializableMeta.getString(map, "blockMaterial", true);
        Material m = Material.getMaterial(matName);
        if (m != null) {
            material = m;
        } else {
            material = Material.AIR;
        }
    }

    @Override
    void applyToItem(NBTTagCompound tag) {
        super.applyToItem(tag);

        if (blockEntityTag != null) {
            tag.set(BLOCK_ENTITY_TAG.NBT, blockEntityTag);
        }
    }

    @Override
    void deserializeInternal(NBTTagCompound tag) {
        if (tag.hasKeyOfType(BLOCK_ENTITY_TAG.NBT, CraftMagicNumbers.NBT.TAG_COMPOUND)) {
            blockEntityTag = tag.getCompound(BLOCK_ENTITY_TAG.NBT);
        }
    }

    @Override
    void serializeInternal(final Map<String, NBTBase> internalTags) {
        if (blockEntityTag != null) {
            internalTags.put(BLOCK_ENTITY_TAG.NBT, blockEntityTag);
        }
    }

    @Override
    ImmutableMap.Builder<String, Object> serialize(ImmutableMap.Builder<String, Object> builder) {
        super.serialize(builder);
        builder.put("blockMaterial", material.name());
        return builder;
    }

    @Override
    int applyHash() {
        final int original;
        int hash = original = super.applyHash();
        if (blockEntityTag != null) {
            hash = 61 * hash + this.blockEntityTag.hashCode();
        }
        return original != hash ? CraftMetaBlockState.class.hashCode() ^ hash : hash;
    }

    @Override
    public boolean equalsCommon(CraftMetaItem meta) {
        if (!super.equalsCommon(meta)) {
            return false;
        }
        if (meta instanceof CraftMetaBlockState) {
            CraftMetaBlockState that = (CraftMetaBlockState) meta;

            return Objects.equal(this.blockEntityTag, that.blockEntityTag);
        }
        return true;
    }

    @Override
    boolean notUncommon(CraftMetaItem meta) {
        return super.notUncommon(meta) && (meta instanceof CraftMetaBlockState || blockEntityTag == null);
    }

    @Override
    boolean isEmpty() {
        return super.isEmpty() && blockEntityTag == null;
    }

    @Override
    boolean applicableTo(Material type) {
        switch(type){
            case FURNACE:
            case CHEST:
            case TRAPPED_CHEST:
            case JUKEBOX:
            case DISPENSER:
            case DROPPER:
            case SIGN:
            case MOB_SPAWNER:
            case NOTE_BLOCK:
            case BREWING_STAND_ITEM:
            case ENCHANTMENT_TABLE:
            case COMMAND:
            case COMMAND_REPEATING:
            case COMMAND_CHAIN:
            case BEACON:
            case DAYLIGHT_DETECTOR:
            case DAYLIGHT_DETECTOR_INVERTED:
            case HOPPER:
            case REDSTONE_COMPARATOR:
            case FLOWER_POT_ITEM:
            case SHIELD:
            case STRUCTURE_BLOCK:
            case WHITE_SHULKER_BOX:
            case ORANGE_SHULKER_BOX:
            case MAGENTA_SHULKER_BOX:
            case LIGHT_BLUE_SHULKER_BOX:
            case YELLOW_SHULKER_BOX:
            case LIME_SHULKER_BOX:
            case PINK_SHULKER_BOX:
            case GRAY_SHULKER_BOX:
            case SILVER_SHULKER_BOX:
            case CYAN_SHULKER_BOX:
            case PURPLE_SHULKER_BOX:
            case BLUE_SHULKER_BOX:
            case BROWN_SHULKER_BOX:
            case GREEN_SHULKER_BOX:
            case RED_SHULKER_BOX:
            case BLACK_SHULKER_BOX:
            case ENDER_CHEST:
                return true;
        }
        return false;
    }

    @Override
    public boolean hasBlockState() {
        return blockEntityTag != null;
    }

    @Override
    public BlockState getBlockState() {
        if (blockEntityTag != null) {
            switch (material) {
                case SHIELD:
                    blockEntityTag.setString("id", "banner");
                    break;
                case WHITE_SHULKER_BOX:
                case ORANGE_SHULKER_BOX:
                case MAGENTA_SHULKER_BOX:
                case LIGHT_BLUE_SHULKER_BOX:
                case YELLOW_SHULKER_BOX:
                case LIME_SHULKER_BOX:
                case PINK_SHULKER_BOX:
                case GRAY_SHULKER_BOX:
                case SILVER_SHULKER_BOX:
                case CYAN_SHULKER_BOX:
                case PURPLE_SHULKER_BOX:
                case BLUE_SHULKER_BOX:
                case BROWN_SHULKER_BOX:
                case GREEN_SHULKER_BOX:
                case RED_SHULKER_BOX:
                case BLACK_SHULKER_BOX:
                    blockEntityTag.setString("id", "shulker_box");
                    break;
            }
        }
        TileEntity te = (blockEntityTag == null) ? null : TileEntity.create(null, blockEntityTag);

        switch (material) {
        case SIGN:
        case SIGN_POST:
        case WALL_SIGN:
            if (te == null) {
                te = new TileEntitySign();
            }
            return new CraftSign(material, (TileEntitySign) te);
        case CHEST:
        case TRAPPED_CHEST:
            if (te == null) {
                te = new TileEntityChest();
            }
            return new CraftChest(material, (TileEntityChest) te);
        case BURNING_FURNACE:
        case FURNACE:
            if (te == null) {
                te = new TileEntityFurnace();
            }
            return new CraftFurnace(material, (TileEntityFurnace) te);
        case DISPENSER:
            if (te == null) {
                te = new TileEntityDispenser();
            }
            return new CraftDispenser(material, (TileEntityDispenser) te);
        case DROPPER:
            if (te == null) {
                te = new TileEntityDropper();
            }
            return new CraftDropper(material, (TileEntityDropper) te);
        case END_GATEWAY:
            if (te == null) {
                te = new TileEntityEndGateway();
            }
            return new CraftEndGateway(material, (TileEntityEndGateway) te);
        case HOPPER:
            if (te == null) {
                te = new TileEntityHopper();
            }
            return new CraftHopper(material, (TileEntityHopper) te);
        case MOB_SPAWNER:
            if (te == null) {
                te = new TileEntityMobSpawner();
            }
            return new CraftCreatureSpawner(material, (TileEntityMobSpawner) te);
        case NOTE_BLOCK:
            if (te == null) {
                te = new TileEntityNote();
            }
            return new CraftNoteBlock(material, (TileEntityNote) te);
        case JUKEBOX:
            if (te == null) {
                te = new BlockJukeBox.TileEntityRecordPlayer();
            }
            return new CraftJukebox(material, (BlockJukeBox.TileEntityRecordPlayer) te);
        case BREWING_STAND_ITEM:
            if (te == null) {
                te = new TileEntityBrewingStand();
            }
            return new CraftBrewingStand(material, (TileEntityBrewingStand) te);
        case SKULL:
            if (te == null) {
                te = new TileEntitySkull();
            }
            return new CraftSkull(material, (TileEntitySkull) te);
        case COMMAND:
        case COMMAND_REPEATING:
        case COMMAND_CHAIN:
            if (te == null) {
                te = new TileEntityCommand();
            }
            return new CraftCommandBlock(material, (TileEntityCommand) te);
        case BEACON:
            if (te == null) {
                te = new TileEntityBeacon();
            }
            return new CraftBeacon(material, (TileEntityBeacon) te);
        case SHIELD:
        case BANNER:
        case WALL_BANNER:
        case STANDING_BANNER:
            if (te == null) {
                te = new TileEntityBanner();
            }
            return new CraftBanner(material, (TileEntityBanner) te);
        case FLOWER_POT_ITEM:
            if (te == null) {
                te = new TileEntityFlowerPot();
            }
            return new CraftFlowerPot(material, (TileEntityFlowerPot) te);
        case STRUCTURE_BLOCK:
            if (te == null) {
                te = new TileEntityStructure();
            }
            return new CraftStructureBlock(material, (TileEntityStructure) te);
        case WHITE_SHULKER_BOX:
        case ORANGE_SHULKER_BOX:
        case MAGENTA_SHULKER_BOX:
        case LIGHT_BLUE_SHULKER_BOX:
        case YELLOW_SHULKER_BOX:
        case LIME_SHULKER_BOX:
        case PINK_SHULKER_BOX:
        case GRAY_SHULKER_BOX:
        case SILVER_SHULKER_BOX:
        case CYAN_SHULKER_BOX:
        case PURPLE_SHULKER_BOX:
        case BLUE_SHULKER_BOX:
        case BROWN_SHULKER_BOX:
        case GREEN_SHULKER_BOX:
        case RED_SHULKER_BOX:
        case BLACK_SHULKER_BOX:
            if (te == null) {
                te = new TileEntityShulkerBox();
            }
            return new CraftShulkerBox(material, (TileEntityShulkerBox) te);
        case ENCHANTMENT_TABLE:
            if (te == null) {
                te = new TileEntityEnchantTable();
            }
            return new CraftEnchantingTable(material, (TileEntityEnchantTable) te);
        case ENDER_CHEST:
            if (te == null){
                te = new TileEntityEnderChest();
            }
            return new CraftEnderChest(material, (TileEntityEnderChest) te);
        case DAYLIGHT_DETECTOR:
        case DAYLIGHT_DETECTOR_INVERTED:
            if (te == null){
                te = new TileEntityLightDetector();
            }
            return new CraftDaylightDetector(material, (TileEntityLightDetector) te);
        case REDSTONE_COMPARATOR:
            if (te == null){
                te = new TileEntityComparator();
            }
            return new CraftComparator(material, (TileEntityComparator) te);
        case PISTON_BASE:
        default:
            throw new IllegalStateException("Missing blockState for " + material);
        }
    }

    @Override
    public void setBlockState(BlockState blockState) {
        Validate.notNull(blockState, "blockState must not be null");

        boolean valid;
        switch (material) {
        case SIGN:
        case SIGN_POST:
        case WALL_SIGN:
            valid = blockState instanceof CraftSign;
            break;
        case CHEST:
        case TRAPPED_CHEST:
            valid = blockState instanceof CraftChest;
            break;
        case BURNING_FURNACE:
        case FURNACE:
            valid = blockState instanceof CraftFurnace;
            break;
        case DISPENSER:
            valid = blockState instanceof CraftDispenser;
            break;
        case DROPPER:
            valid = blockState instanceof CraftDropper;
            break;
        case END_GATEWAY:
            valid = blockState instanceof CraftEndGateway;
            break;
        case HOPPER:
            valid = blockState instanceof CraftHopper;
            break;
        case MOB_SPAWNER:
            valid = blockState instanceof CraftCreatureSpawner;
            break;
        case NOTE_BLOCK:
            valid = blockState instanceof CraftNoteBlock;
            break;
        case JUKEBOX:
            valid = blockState instanceof CraftJukebox;
            break;
        case BREWING_STAND_ITEM:
            valid = blockState instanceof CraftBrewingStand;
            break;
        case SKULL:
            valid = blockState instanceof CraftSkull;
            break;
        case COMMAND:
        case COMMAND_REPEATING:
        case COMMAND_CHAIN:
            valid = blockState instanceof CraftCommandBlock;
            break;
        case BEACON:
            valid = blockState instanceof CraftBeacon;
            break;
        case SHIELD:
        case BANNER:
        case WALL_BANNER:
        case STANDING_BANNER:
            valid = blockState instanceof CraftBanner;
            break;
        case FLOWER_POT_ITEM:
            valid = blockState instanceof CraftFlowerPot;
            break;
        case STRUCTURE_BLOCK:
            valid = blockState instanceof CraftStructureBlock;
            break;
        case WHITE_SHULKER_BOX:
        case ORANGE_SHULKER_BOX:
        case MAGENTA_SHULKER_BOX:
        case LIGHT_BLUE_SHULKER_BOX:
        case YELLOW_SHULKER_BOX:
        case LIME_SHULKER_BOX:
        case PINK_SHULKER_BOX:
        case GRAY_SHULKER_BOX:
        case SILVER_SHULKER_BOX:
        case CYAN_SHULKER_BOX:
        case PURPLE_SHULKER_BOX:
        case BLUE_SHULKER_BOX:
        case BROWN_SHULKER_BOX:
        case GREEN_SHULKER_BOX:
        case RED_SHULKER_BOX:
        case BLACK_SHULKER_BOX:
            valid = blockState instanceof CraftShulkerBox;
            break;
        case ENCHANTMENT_TABLE:
            valid = blockState instanceof CraftEnchantingTable;
            break;
        case ENDER_CHEST:
            valid = blockState instanceof CraftEnderChest;
            break;
        case DAYLIGHT_DETECTOR:
        case DAYLIGHT_DETECTOR_INVERTED:
            valid = blockState instanceof CraftDaylightDetector;
            break;
        case REDSTONE_COMPARATOR:
            valid = blockState instanceof CraftComparator;
            break;
        default:
            valid = false;
            break;
        }

        Validate.isTrue(valid, "Invalid blockState for " + material);

        blockEntityTag = ((CraftBlockEntityState) blockState).getSnapshotNBT();
    }
}
