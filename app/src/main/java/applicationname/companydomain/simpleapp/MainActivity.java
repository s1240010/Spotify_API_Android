package applicationname.companydomain.simpleapp;

import android.support.v7.app.AppCompatActivity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import com.spotify.sdk.android.authentication.AuthenticationClient;
import com.spotify.sdk.android.authentication.AuthenticationRequest;
import com.spotify.sdk.android.authentication.AuthenticationResponse;
import com.spotify.sdk.android.player.Config;
import com.spotify.sdk.android.player.ConnectionStateCallback;
import com.spotify.sdk.android.player.Error;
import com.spotify.sdk.android.player.Player;
import com.spotify.sdk.android.player.PlayerEvent;
import com.spotify.sdk.android.player.Spotify;
import com.spotify.sdk.android.player.SpotifyPlayer;

public class MainActivity extends AppCompatActivity implements
        SpotifyPlayer.NotificationCallback, ConnectionStateCallback
{

    private static final String CLIENT_ID = "bbcc3bab47254fb1808d1ede408946ec";
    private  static final String REDIRECT_URL = "satoko://callback";
    private Player mPlayer;
    private static final int REQUEST_CODE = 1337;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        AuthenticationRequest.Builder builder = new AuthenticationRequest.Builder(CLIENT_ID,
                AuthenticationResponse.Type.TOKEN,REDIRECT_URL);
        builder.setScopes(new String[]{"user-read-private","streaming"});
        AuthenticationRequest request = builder.build();

        AuthenticationClient.openLoginActivity(this,REQUEST_CODE,request);
    }

    @Override
    protected  void onActivityResult(int requestCode, int resultCode,Intent intent){
        super.onActivityResult(requestCode,resultCode,intent);

        if(requestCode ==  REQUEST_CODE){
            AuthenticationResponse response = AuthenticationClient.getResponse(resultCode,intent);
            if(response.getType() ==  AuthenticationResponse.Type.TOKEN){
                Config playerConfig = new Config(this, response.getAccessToken(),CLIENT_ID);
                Spotify.getPlayer(playerConfig,this, new SpotifyPlayer.InitializationObserver(){
                    @Override
                    public void onInitialized(SpotifyPlayer spotifyPlayer){
                        mPlayer = spotifyPlayer;
                        mPlayer.addConnectionStateCallback(MainActivity.this);
                        mPlayer.addNotificationCallback(MainActivity.this);
                    }
                    @Override
                    public void onError(Throwable throwable){
                        Log.e("MainActivity","Could not initialize player: "+
                        throwable.getMessage());
                    }
                });
            }
        }
    }


    @Override
    protected  void onDestroy(){
        super.onDestroy();
        Spotify.destroyPlayer(this);
        super.onDestroy();
    }

    @Override
    public void onPlaybackEvent(PlayerEvent playerEvent){
        Log.d("MainActivity","Playback event received" + playerEvent.name());
        switch (playerEvent){
            default:
                break;
        }
    }

    @Override
    public void onPlaybackError(Error error){
        Log.d("MainActivity","Playback error received:" + error.name());
        switch(error){
            default:
                break;
        }
    }

    @Override
    public void onLoggedIn(){
        Log.d("MainActivity","User logged in");
        mPlayer.playUri(null,"spotify:track:2TpxZ7JUBn3uw46aR7qd6V",0,0);
    }

    @Override
    public void onLoggedOut(){
        Log.d("MainActivity","User logged out");
    }

    @Override
    public void onLoginFailed(Error val1){
        Log.d("MainActivity","Temporary error occurred");
    }

    @Override
    public void onTemporaryError(){
        Log.d("MainActivity","Temporary error occurred");
    }

    @Override
    public void onConnectionMessage(String message){
        Log.d("MainActivity","Received connection message" + message);
    }


}
