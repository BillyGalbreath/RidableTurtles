package net.pl3x.bukkit.ridableturtles;

import com.google.common.collect.HashBiMap;
import net.minecraft.server.v1_13_R1.EntityTypes;
import net.minecraft.server.v1_13_R1.Item;
import net.minecraft.server.v1_13_R1.ItemMonsterEgg;
import net.minecraft.server.v1_13_R1.MinecraftKey;
import net.minecraft.server.v1_13_R1.RegistryID;
import net.minecraft.server.v1_13_R1.RegistryMaterials;
import net.minecraft.server.v1_13_R1.RegistrySimple;
import net.minecraft.server.v1_13_R1.World;
import net.pl3x.bukkit.ridableturtles.command.CmdRidableTurtles;
import net.pl3x.bukkit.ridableturtles.configuration.Config;
import net.pl3x.bukkit.ridableturtles.configuration.Lang;
import net.pl3x.bukkit.ridableturtles.entity.EntityRidableTurtle;
import net.pl3x.bukkit.ridableturtles.listener.TurtleListener;
import org.bstats.bukkit.Metrics;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.craftbukkit.v1_13_R1.inventory.CraftItemStack;
import org.bukkit.entity.EntityType;
import org.bukkit.event.Listener;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.java.JavaPlugin;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.Map;
import java.util.function.Function;

public class RidableTurtles extends JavaPlugin implements Listener {
    @Override
    public void onLoad() {
        EntityTypes.a<EntityRidableTurtle> type = EntityTypes.a.a(EntityRidableTurtle.class, (Function<? super World, ? extends EntityRidableTurtle>) EntityRidableTurtle::new);
        EntityTypes<EntityRidableTurtle> types = type.a("turtle");
        MinecraftKey key = new MinecraftKey("turtle");
        try {
            EntityTypes.clsToKeyMap.put(EntityRidableTurtle.class, key);
            EntityTypes.clsToTypeMap.put(EntityRidableTurtle.class, EntityType.fromName("turtle"));
        } catch (NoSuchFieldError ignore) {
        }
        try {
            Field registryMaterials_fieldA = RegistryMaterials.class.getDeclaredField("a");
            registryMaterials_fieldA.setAccessible(true);
            RegistryID<EntityTypes<?>> registryID = (RegistryID<EntityTypes<?>>) registryMaterials_fieldA.get(EntityTypes.REGISTRY);
            int originalID = registryID.getId(EntityTypes.TURTLE);
            Method registryID_methodD = RegistryID.class.getDeclaredMethod("d", Object.class);
            registryID_methodD.setAccessible(true);
            int newIndex = (int) registryID_methodD.invoke(registryID, types);
            Field registryID_fieldB = RegistryID.class.getDeclaredField("b");
            registryID_fieldB.setAccessible(true);
            Object[] arrB = (Object[]) registryID_fieldB.get(registryID);
            arrB[newIndex] = types;
            int oldIndex = -1;
            for (int i = 0; i < arrB.length; i++) {
                if (arrB[i] == EntityTypes.TURTLE) {
                    arrB[i] = null;
                    oldIndex = i;
                    break;
                }
            }
            registryID_fieldB.set(registryID, arrB);
            Field registryID_fieldC = RegistryID.class.getDeclaredField("c");
            registryID_fieldC.setAccessible(true);
            int[] arrC = (int[]) registryID_fieldC.get(registryID);
            arrC[oldIndex] = 0;
            arrC[newIndex] = originalID;
            registryID_fieldC.set(registryID, arrC);
            Field registryID_fieldD = RegistryID.class.getDeclaredField("d");
            registryID_fieldD.setAccessible(true);
            Object[] arrD = (Object[]) registryID_fieldD.get(registryID);
            arrD[originalID] = types;
            registryID_fieldD.set(registryID, arrD);
            registryMaterials_fieldA.set(EntityTypes.REGISTRY, registryID);
            Field registryId_b = RegistryMaterials.class.getDeclaredField("b");
            registryId_b.setAccessible(true);
            Map<EntityTypes<?>, MinecraftKey> mapB_original = (Map<EntityTypes<?>, MinecraftKey>) registryId_b.get(EntityTypes.REGISTRY);
            Map<EntityTypes<?>, MinecraftKey> mapB_replacement = HashBiMap.create();
            for (Map.Entry<EntityTypes<?>, MinecraftKey> entry : mapB_original.entrySet()) {
                if (entry.getKey() != EntityTypes.TURTLE) {
                    mapB_replacement.put(entry.getKey(), entry.getValue());
                } else {
                    mapB_replacement.put(types, key);
                }
            }
            registryId_b.set(EntityTypes.REGISTRY, mapB_replacement);
            Field registrySimple_fieldC = RegistrySimple.class.getDeclaredField("c");
            registrySimple_fieldC.setAccessible(true);
            Map<MinecraftKey, EntityTypes<?>> mapC = (Map<MinecraftKey, EntityTypes<?>>) registrySimple_fieldC.get(EntityTypes.REGISTRY);
            mapC.put(key, types);
            registrySimple_fieldC.set(EntityTypes.REGISTRY, mapC);
            Field entityTypes_fieldTURTLE = EntityTypes.class.getField("TURTLE");
            entityTypes_fieldTURTLE.setAccessible(true);
            Field modifiersField = Field.class.getDeclaredField("modifiers");
            modifiersField.setAccessible(true);
            modifiersField.setInt(entityTypes_fieldTURTLE, entityTypes_fieldTURTLE.getModifiers() & ~Modifier.FINAL);
            entityTypes_fieldTURTLE.set(null, types);
        } catch (NoSuchFieldException | IllegalAccessException | NoSuchMethodException | InvocationTargetException e) {
            e.printStackTrace();
        }

        Item turtleSpawnEgg = CraftItemStack.asNMSCopy(new ItemStack(Material.TURTLE_SPAWN_EGG)).getItem();
        try {
            Field field_d = ItemMonsterEgg.class.getDeclaredField("d");
            field_d.setAccessible(true);
            field_d.set(turtleSpawnEgg, EntityTypes.TURTLE);
        } catch (NoSuchFieldException | IllegalAccessException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onEnable() {
        Config.reload();
        Lang.reload();

        try {
            Class.forName("org.bukkit.entity.Turtle");
            Class.forName("org.spigotmc.event.entity.EntityDismountEvent");
        } catch (ClassNotFoundException e) {
            ConsoleCommandSender console = getServer().getConsoleSender();
            console.sendMessage(ChatColor.RED + "This server is unsupported!");
            console.sendMessage(ChatColor.RED + "Please use Spigot or Paper version 1.13 or higher!");
            console.sendMessage(ChatColor.RED + "Plugin is now disabling itself!");
            getServer().getPluginManager().disablePlugin(this);
            return;
        }

        getServer().getPluginManager().registerEvents(new TurtleListener(), this);

        getCommand("ridableturtles").setExecutor(new CmdRidableTurtles(this));

        Metrics metrics = new Metrics(this);
        metrics.addCustomChart(new Metrics.SimplePie("server_version", () -> {
            try {
                Class.forName("com.destroystokyo.paper.PaperConfig");
                return "Paper";
            } catch (Exception ignore) {
            }
            try {
                Class.forName("org.spigotmc.SpigotConfig");
                return "Spigot";
            } catch (Exception ignore2) {
            }
            return "CraftBukkit";
        }));
    }
}
