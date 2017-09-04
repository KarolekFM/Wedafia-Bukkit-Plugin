package eu.devy.wedafia.json;

import com.lishid.openinv.OpenInv;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.PotionMeta;
import org.bukkit.enchantments.Enchantment;

import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.json.simple.JSONObject;
import org.json.simple.JSONArray;

import java.util.*;

public class PlayerJSON {
    private static OpenInv openinv;

    public static void setOpenInvInstance(OpenInv openinv) {
        PlayerJSON.openinv = openinv;
    }

    public static JSONObject getPlayeyInfo(String player_string) {
        Player player = openinv.loadPlayer(openinv.matchPlayer(player_string));

        JSONObject json = new JSONObject();

        if (player != null) {
            json.put("is_online", player.isOnline());
            json.put("display_name", player.getDisplayName());
            json.put("experience", player.getTotalExperience());
            json.put("food_level", player.getFoodLevel());
            json.put("health_level", player.getHealth());
            json.put("is_sneaking", player.isSneaking());

            json.put("inventory", getPlayerInv(Optional.empty(), Optional.of(player)));
        }

        return json;
    }

    public static JSONArray getPlayerInv(Optional<String> player_string_o, Optional<Player> player_o) {
        Player player = null;

        if (player_o.isPresent()) {
             player = player_o.get();
        } else if (player_string_o.isPresent()) {
            player = openinv.loadPlayer(openinv.matchPlayer(player_string_o.get()));
        }

        JSONArray json = new JSONArray();

        if (player != null) {
            Inventory inv = player.getInventory();
            Integer inv_size = inv.getSize();

            for (Byte slot = 0; slot < inv_size; slot++) {
                JSONObject json_item = new JSONObject();

                ItemStack item = inv.getItem(slot);

                if (item != null) {
                    json_item.put("slot", slot);
                    json_item.put("type", item.getType().name());
                    json_item.put("durability", item.getDurability());
                    json_item.put("ammount", item.getAmount());

                    json_item.put("meta", getItemMeta(item));

                    json.add(json_item);
                }
            }
        }

        return json;
    }

    private static JSONObject getItemMeta(ItemStack item) {
        JSONObject json = new JSONObject();

        ItemMeta meta = item.getItemMeta();

        json.put("display_name", meta.getDisplayName());
        json.put("is_unbreakable", meta.isUnbreakable());

        List<String> lore = meta.getLore();

        if(lore != null) {
            json.put("lore", lore);
        }

        JSONArray json_enchants = new JSONArray();

        for (Map.Entry<Enchantment, Integer> enchant : meta.getEnchants().entrySet()) {
            JSONObject json_enchant = new JSONObject();

            json_enchant.put(enchant.getKey().getName(), enchant.getValue());
            json_enchants.add(json_enchant);
        }

        json.put("enchantments", json_enchants);

        if(
            item.getType() == Material.POTION ||
            item.getType() == Material.LINGERING_POTION ||
            item.getType() == Material.SPLASH_POTION
        ) {
            json.put("effect", getPotionMeta((PotionMeta)meta));
        }

        return json;
    }

    private static JSONObject getPotionMeta(PotionMeta meta) {
        JSONObject json = new JSONObject();

        PotionEffectType effect = meta.getBasePotionData().getType().getEffectType();
        json.put("type", effect.getName());
        json.put("is_instant", effect.isInstant());
        json.put("duration_modifier", effect.getDurationModifier());

        JSONArray json_custom_effects = new JSONArray();
        List<PotionEffect> custom_effects = meta.getCustomEffects();

        for(PotionEffect custom_effect : custom_effects) {
            JSONObject json_custom_effect = new JSONObject();
            json_custom_effect.put("type", custom_effect.getType());
            json_custom_effect.put("amplifier",custom_effect.getAmplifier());
            json_custom_effect.put("duration", custom_effect.getDuration());
            json_custom_effect.put("color", custom_effect.getColor());

            json_custom_effects.add(json_custom_effects);
        }

        json.put("custom", json_custom_effects);

        return json;
    }
}
