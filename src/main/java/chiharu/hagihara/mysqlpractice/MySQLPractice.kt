package chiharu.hagihara.mysqlpractice

import org.bukkit.Bukkit
import org.bukkit.command.CommandSender
import org.bukkit.command.ConsoleCommandSender
import org.bukkit.entity.Player
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
        saveResource("config.yml", false)

        getCommand("memo")?.setExecutor(this)
        server.pluginManager.registerEvents(this, this)
    }

    override fun onDisable() { // Plugin shutdown logic
    }

    @EventHandler
    fun onJoin(e: PlayerJoinEvent){
        val mysql = MySQLManager(MySQLPractice(), "mysqlpractice")
        try {
            var select = mysql.query("select * from mysqlpractice_memos where uuid = '${e.player.uniqueId}'")

            select?.next()
            if (select == null){
                mysql.close()
                Bukkit.broadcastMessage("$prefix §c§lデータベース接続エラー")
                return
            }

            var name = select.getString("name")

            if (name == e.player.name)return

            var update = mysql.execute("update mysqlpractice_memos set name = '${e.player.name}' where uuid = '${e.player.uniqueId}'")

            if (!update){
                e.player.sendMessage("$prefix §c§l例外発生！")
            }
            update
            Bukkit.broadcastMessage("$prefix §a§l${e.player.displayName}のMCID変更処理完了！")
        }catch (e:Exception) {
            mysql.close()
            Bukkit.broadcastMessage("$prefix §c§l例外発生！")
        }
        mysql.close()
    }

    override fun onCommand(sender: CommandSender, command: org.bukkit.command.Command, label: String, args: Array<out String>): Boolean {
        if (sender is ConsoleCommandSender)return false

        if (args.isEmpty())return false

        if (args.size != 2)return false

        val cmd = args[0]
        val memo = args[1]
        val p = sender as Player

        val mysql = MySQLManager(MySQLPractice(), "mysqlpractice")

        when (cmd) {
            "help" -> showHelp(p)
            "create" -> {
                try {
                    var select = mysql.query("select * from mysqlpractice_memos where uuid = '${p.uniqueId}'")

                    select?.next()
                    if (select == null){
                        mysql.close()
                        Bukkit.broadcastMessage("$prefix §c§lデータベース接続エラー")
                        return false
                    }

                    var name = select.getString("name")

                    if (name == p.name){
                        p.sendMessage("$prefix §c§lあなたはすでにMEMOを作っています！")
                        return false
                    }

                    var update = mysql.execute("update mysqlpractice_memos set memo = '${memo}' where uuid = '${p.uniqueId}'")

                    if (!update){
                        p.sendMessage("$prefix §c§l例外発生！")
                    }
                    update
                    p.sendMessage("$prefix §a§lMEMOの作成完了！")
                }catch (e:Exception) {
                    mysql.close()
                    Bukkit.broadcastMessage("$prefix §c§l例外発生！")
                }
                mysql.close()
            }
            "delete" -> {

            }
            "edit" -> {

            }
            "read" -> {

            }
            else -> {

            }
        }
        return true
    }

    fun showHelp(p: Player){
        p.sendMessage("$prefix Memo使い方 powered by MySQLPractice")
        p.sendMessage("$prefix §f§l/memo create <MEMOする内容>")
        p.sendMessage("$prefix §f§l/memo delete <MCID>")
        p.sendMessage("$prefix §f§l/memo read §7<MCID>")
        p.sendMessage("$prefix §f§l/memo edit <書き換えたい内容>")
    }
}