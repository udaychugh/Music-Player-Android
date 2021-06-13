package com.udaychugh.musicplayer

import android.Manifest
import android.content.pm.PackageManager
import android.media.MediaPlayer
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.MediaStore
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.Toast
import androidx.core.app.ActivityCompat
import java.lang.Exception
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.song_ticket.view.*


class MainActivity : AppCompatActivity() {

    var listSongs=ArrayList<SongInfo>()
    var adapter:MySongAdapter?=null
    var mp:MediaPlayer?=null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        LoadURLOnline()
        CheckUserPermision()

        var mytracking=mySongTrack()
        mytracking.start()

    }

    fun LoadURLOnline()
    {
        listSongs.add(SongInfo("BackBone", "Hardy Sandhu", "https://github.com/udaychugh/music-player/blob/master/music/Backbone.mp3?raw=true"))
        listSongs.add(SongInfo("Faded", "Alan Walker", "https://github.com/udaychugh/music-player/blob/master/music/Faded.mp3?raw=true"))
        listSongs.add(SongInfo("Heartless", "Badshah", "https://github.com/udaychugh/music-player/blob/master/music/Heartless.mp3?raw=true"))
        listSongs.add(SongInfo("Khaab", "Akhil", "https://github.com/udaychugh/music-player/blob/master/music/Khaab.mp3?raw=true"))
        listSongs.add(SongInfo("Photo", "Unknown", "https://github.com/udaychugh/music-player/blob/master/music/Photo.mp3?raw=true"))
    }
    inner class MySongAdapter:BaseAdapter {
        var myListSong=ArrayList<SongInfo>()
        constructor(myListSong:ArrayList<SongInfo>):super(){
            this.myListSong=myListSong
        }
        override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {
            val myView = layoutInflater.inflate(R.layout.song_ticket, null)
            val songs = this.myListSong[position]
            myView.tvSongName.text = songs.Title
            myView.tvAuthor.text = songs.AuthorName

            myView.buPlay.setOnClickListener{
                if (myView.buPlay.text == "Stop"){
                    mp!!.stop()
                    myView.buPlay.text="Start"
                } else{
                    mp = MediaPlayer()
                    try {
                        mp!!.setDataSource(songs.SongUrl)
                        mp!!.prepare()
                        mp!!.start()
                        myView.buPlay.text = "Stop"
                        sbProgress.max=mp!!.duration
                    }catch (
                        ex: Exception
                    ){}
                }
            }
            return myView

        }

        override fun getItem(item: Int): Any {
            return this.myListSong[item]
        }

        override fun getItemId(po: Int): Long {
            return po.toLong()
        }

        override fun getCount(): Int {
            return this.myListSong.size
        }

    }

    inner  class mySongTrack: Thread(){
        override fun run() {
            while(true){
                try {
                    sleep(1000)
                }catch (ex:Exception){}

                runOnUiThread {
                    if (mp!=null){
                        sbProgress.progress = mp!!.currentPosition
                    }
                }
            }
        }
    }

    fun CheckUserPermision() {
        if (Build.VERSION.SDK_INT >= 23) {
            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                requestPermissions(arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE),
                REQUEST_CODE_ASK_PERMISSIONS)
                return
            }
        }
        LoadSong()
    }
    private val REQUEST_CODE_ASK_PERMISSIONS = 123

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        when(requestCode){
            REQUEST_CODE_ASK_PERMISSIONS -> if (grantResults[0] == PackageManager.PERMISSION_GRANTED){
                LoadSong()
            } else{
                Toast.makeText(this, "Give Persmission First",Toast.LENGTH_SHORT).show()
            }
            else -> super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        }
    }

    fun LoadSong()
    {
        val allSongsURI = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI
        val selection = MediaStore.Audio.Media.IS_MUSIC + "!=0"
        val cursor = contentResolver.query(allSongsURI, null,  selection, null, null)
        if (cursor != null) {
            if(cursor!!.moveToFirst()) {
                do {
                    val songURL = cursor!!.getString(cursor!!.getColumnIndex(MediaStore.Audio.Media.DATA))
                    val songAuthor = cursor!!.getString(cursor!!.getColumnIndex(MediaStore.Audio.Media.ARTIST))
                    val SongName = cursor!!.getString(cursor!!.getColumnIndex(MediaStore.Audio.Media.DISPLAY_NAME))
                    listSongs.add(SongInfo(SongName, songAuthor, songURL))
                } while (cursor!!.moveToNext())
            }
            cursor!!.close()
            adapter=MySongAdapter(listSongs)
            lsListSongs.adapter=adapter


        }
    }
}


