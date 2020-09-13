package chiharu.hagihara.mysqlpractice

import org.bukkit.Bukkit
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.player.PlayerJoinEvent
import org.bukkit.plugin.java.JavaPlugin

class MySQLPractice : JavaPlugin() , Listener{

    companion object{
        val prefix = "§e§l[MySQLPractice]"
    }

    override fun onEnable() { // Plugin startup logic
        saveDefaultConfig()

        server.pluginManager.registerEvents(this, this)
    }

    override fun onDisable() { // Plugin shutdown logic
    }

    @EventHandler
    fun onLogin(e: PlayerJoinEvent) {
        Thread(Runnable {
            val p = e.player
            val address = p.address.hostString

            if (address == null) {
                logger.info("${p.name}のIPアドレスの取得に失敗！")
                return@Runnable
            }

            val mysql = MySQLManager(this, "MySQLPractice")
            val rs = mysql.query("SELECT * FROM mp_loginlog WHERE uuid='${p.uniqueId}';") ?: return@Runnable

            if (rs.next()) {
                mysql.execute("UPDATE mp_loginlog SET mcid='${p.name}', address='${address}' WHERE uuid='${p.uniqueId}';")

                rs.close()
                mysql.close()

                return@Runnable
            }

            rs.close()
            mysql.close()

            mysql.execute("INSERT INTO mp_loginlog (mcid,uuid,address) VALUES ('${p.name}','${p.uniqueId}','${address}');")
         }).start()
    }

}