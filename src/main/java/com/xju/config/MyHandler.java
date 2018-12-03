//package com.xju.config;
//
//import java.io.IOException;
//import java.util.List;
//
//import org.springframework.web.socket.CloseStatus;
//import org.springframework.web.socket.TextMessage;
//import org.springframework.web.socket.WebSocketSession;
//import org.springframework.web.socket.handler.TextWebSocketHandler;
//
//import com.fasterxml.jackson.core.JsonProcessingException;
//import com.fasterxml.jackson.databind.ObjectMapper;
//import com.init.Storage;
//import com.pojo.SimuTask;
//import com.simu.Simulator;
//
///**
// * 
// * @author PengBin
// * @date 2016年6月24日 下午6:04:39
// */
//public class MyHandler extends TextWebSocketHandler {
//	private WebSocketSession session;
//
//	public MyHandler() {
//	}
//
//	@Override
//	public void afterConnectionEstablished(WebSocketSession session) throws Exception {
//		this.session = session;
//	}
//
//	@Override
//	protected void handleTextMessage(WebSocketSession session, TextMessage message) throws Exception {
//
//		String uuID = message.getPayload();
//		if (uuID == null || uuID == "") {
//			return;
//		}
//		System.out.println("收到"+uuID+",准备回传");
//		
//		sendAtRate(uuID);
//	}
//
//	public void sendAtRate(String uid) {
//		try {
//			while (true) {
//				Thread.sleep(1000);
//				Simulator simuler = Storage.getSimuler(uid);
//				if (simuler == null) {
//					break;
//				}
//				List<SimuTask> tasks = simuler.getState();
//				if (tasks == null) {
//					continue;
//				}
//				String mesgStr=produceMessage(tasks);
//				TextMessage resp = new TextMessage(mesgStr);
//				System.out.println("fasong--------------");
//				this.session.sendMessage(resp);
//
//			}
//		} catch (IOException e) {
//			e.printStackTrace();
//		} catch (InterruptedException e) {
//			e.printStackTrace();
//		}
//	}
//	
//	public String produceMessage(List<SimuTask> tasks) {
//		
//		ObjectMapper mapper=new ObjectMapper();
//		try {
//			return mapper.writeValueAsString(tasks);
//		} catch (JsonProcessingException e) {
//			e.printStackTrace();
//		}
//		return null;
//	
//	}
//
//	@Override
//	public void afterConnectionClosed(WebSocketSession session, CloseStatus status) throws Exception {
//		System.out.println("链接关闭了！");
//	}
//}
