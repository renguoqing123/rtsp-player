package com.rtsp;

import java.nio.ByteBuffer;
import java.util.HashMap;
import java.util.List;

import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.socket.BinaryMessage;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
public class HandleRtspWebSocker implements IHandleWebSocket  {

    public void afterConnectionEstablished(WebSocketSession session) throws Exception {
        log.info("Rtsp Sesson start:" + session.getId());
    }
    public void handleBinaryMessage(WebSocketSession session, BinaryMessage message) {
        ByteBuffer bb = message.getPayload();
        byte[] data = bb.array();
        log.info("Recv from RTP session:[" + session.getId() + "]\r\n" + new String(data));
    }
    public void handleTextMessage(WebSocketSession session, TextMessage message) {
        try {
            String str_msg = message.getPayload();
            log.info("Recv from RTSP session:[" + session.getId() + "]\r\n" + str_msg);

            SessionInfo sessionInfo = SessionInfo.get(session.getId());
            if (sessionInfo == null) {
                List<String> lines = Utils.msg2lines(str_msg);
                HashMap<String, String> hsmpVal = Utils.list2key(lines, ":", 1);

                String host = hsmpVal.get("host".toLowerCase());
                String sport = hsmpVal.get("port".toLowerCase());
                String url = hsmpVal.get("url".toLowerCase());
                String seq = hsmpVal.get("seq".toLowerCase());
                if (StringUtils.isEmpty(host) || StringUtils.isEmpty(sport)) {
                    session.sendMessage(new TextMessage("UNKNOWN\r\n\r\n"));
                    return;
                }
                int port = Integer.parseInt(sport);

                sessionInfo = new SessionInfo();
                sessionInfo.setHost(host);
                sessionInfo.setPort(port);
                sessionInfo.setUrl(url);
                SessionInfo.add(session.getId(), sessionInfo);
                sessionInfo.setLocalRtspService(new LocalRtspService((sessionInfo)));
                sessionInfo.setRemoteRtspService(new RemoteRtspService(sessionInfo));
                sessionInfo.getLocalRtspService().attachRtspChannel(session);
                sessionInfo.getRemoteRtspService().start();
                sessionInfo.getLocalRtspService().start();

                String channel = sessionInfo.getChannel();
                String channelStr = "localhost-" + channel + " " + channel;
                String s = WebSocketConfig.PROXY_PROTOCOL + "/" + WebSocketConfig.PROXY_VERSION + " 200 OK" + "\r\n"
                        + "channel: " + channelStr + "\r\n"
                        + "seq: " + seq + "\r\n"
                        + "\r\n";
                session.sendMessage(new TextMessage(s));
                log.info("[Send to RTSP Channel]\r\n" + s);
                return;
            } else {
                sessionInfo.getLocalRtspService().recvMsg(str_msg);
            }
        } catch(Exception e) {
        }
    }
    public void afterConnectionClosed(WebSocketSession session, CloseStatus status) throws Exception {
        SessionInfo sessionInfo = SessionInfo.get(session.getId());
        if(sessionInfo != null) {
            SessionInfo.remove(session.getId());
            sessionInfo.close();
            log.info("RTSP Sesson closed:" + session.getId());
        }
    }
}
