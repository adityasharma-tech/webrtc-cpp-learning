package tech.adityasharma.prodwebrtc;

import android.content.Intent;
import android.os.Bundle;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import org.webrtc.AudioSource;
import org.webrtc.AudioTrack;
import org.webrtc.Camera2Enumerator;
import org.webrtc.DataChannel;
import org.webrtc.DefaultVideoDecoderFactory;
import org.webrtc.DefaultVideoEncoderFactory;
import org.webrtc.EglBase;
import org.webrtc.IceCandidate;
import org.webrtc.MediaConstraints;
import org.webrtc.MediaStream;
import org.webrtc.PeerConnection;
import org.webrtc.PeerConnectionFactory;
import org.webrtc.SurfaceTextureHelper;
import org.webrtc.SurfaceViewRenderer;
import org.webrtc.VideoCapturer;
import org.webrtc.VideoSource;
import org.webrtc.VideoTrack;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    private PeerConnectionFactory peerConnectionFactory;
    private PeerConnection peerConnection;

    private EglBase eglBase;

    private SurfaceViewRenderer localView;
    private VideoCapturer videoCapturer;
    private VideoSource videoSource;
    private VideoTrack localVideoTrack;
    private AudioSource audioSource;
    private AudioTrack localAudioTrack;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        Intent productionServiceIntent = new Intent(this, ProductionService.class);
        startService(productionServiceIntent);
    }

    private void initializeWebRTC(){
        PeerConnectionFactory.InitializationOptions initializationOptions = PeerConnectionFactory.InitializationOptions.builder(this)
                .setEnableInternalTracer(true)
                .createInitializationOptions();
        PeerConnectionFactory.initialize(initializationOptions);
        eglBase = EglBase.create();
        PeerConnectionFactory.Options factoryOptions = new PeerConnectionFactory.Options();

        peerConnectionFactory = PeerConnectionFactory.builder()
                .setOptions(factoryOptions)
                .setVideoEncoderFactory(new DefaultVideoEncoderFactory(eglBase.getEglBaseContext(), true, true))
                .setVideoDecoderFactory(new DefaultVideoDecoderFactory(eglBase.getEglBaseContext()))
                .createPeerConnectionFactory();
    }

    private VideoCapturer createCameraCapturer() {
        Camera2Enumerator enumerator = new Camera2Enumerator(this);
        for (String deviceName : enumerator.getDeviceNames()) {
            if (enumerator.isFrontFacing(deviceName)) {
                VideoCapturer capturer = enumerator.createCapturer(deviceName, null);
                if (capturer != null) return capturer;
            }
        }
        return null;
    }

    private void createLocalMediaTracks(){
        videoCapturer = createCameraCapturer();
        assert videoCapturer != null;
        videoSource = peerConnectionFactory.createVideoSource(videoCapturer.isScreencast());
        SurfaceTextureHelper surfaceTextureHelper = SurfaceTextureHelper.create("CaptureThread", eglBase.getEglBaseContext());

        videoCapturer.initialize(surfaceTextureHelper, this, videoSource.getCapturerObserver());
        localVideoTrack = peerConnectionFactory.createVideoTrack("LOCAL_VIDEO", videoSource);

        audioSource = peerConnectionFactory.createAudioSource(new MediaConstraints());
        localAudioTrack = peerConnectionFactory.createAudioTrack("LOCAL_AUDIO", audioSource);
    }

    private void startLocalVideo() {
        videoCapturer.startCapture(1280, 720, 30);
        localVideoTrack.addSink(localView);
    }

    private void createPeerConnection(){
        PeerConnection.RTCConfiguration rtcConfiguration = new PeerConnection.RTCConfiguration(new ArrayList<>());

        peerConnection = peerConnectionFactory.createPeerConnection(rtcConfiguration, new PeerConnection.Observer() {
            @Override
            public void onSignalingChange(PeerConnection.SignalingState newState) {

            }

            @Override
            public void onIceConnectionChange(PeerConnection.IceConnectionState newState) {

            }

            @Override
            public void onIceConnectionReceivingChange(boolean receiving) {

            }

            @Override
            public void onIceGatheringChange(PeerConnection.IceGatheringState newState) {

            }

            @Override
            public void onIceCandidate(IceCandidate candidate) {

            }

            @Override
            public void onIceCandidatesRemoved(IceCandidate[] candidates) {

            }

            @Override
            public void onAddStream(MediaStream stream) {

            }

            @Override
            public void onRemoveStream(MediaStream stream) {

            }

            @Override
            public void onDataChannel(DataChannel dataChannel) {

            }

            @Override
            public void onRenegotiationNeeded() {

            }
        });

        MediaStream mediaStream = peerConnectionFactory.createLocalMediaStream("LOCAL_STREAM");
        mediaStream.addTrack(localVideoTrack);
        mediaStream.addTrack(localAudioTrack);
        peerConnection.addStream(mediaStream);
    }
}