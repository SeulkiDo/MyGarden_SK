package my.garden.dao;


import java.io.BufferedReader;
import java.io.File;
import java.io.InputStreamReader;
import java.math.BigInteger;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import javax.mail.Message;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.message.BasicNameValuePair;
import org.codehaus.jackson.JsonNode;
import org.codehaus.jackson.map.ObjectMapper;
import org.mybatis.spring.SqlSessionTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.web.multipart.MultipartFile;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import my.garden.dto.CalendarDTO;
import my.garden.dto.MembersDTO;
import my.garden.dto.ShopListDTO;

@Repository
public class LoginDAO {

	@Autowired
	private SqlSessionTemplate sst;
	@Autowired
	HttpServletRequest request;
	@Autowired
	HttpSession session;
	
	public static String SHA256(String str){
		String SHA = ""; 
		try{
			MessageDigest sh = MessageDigest.getInstance("SHA-256"); 
			sh.update(str.getBytes()); 
			byte byteData[] = sh.digest();
			StringBuffer sb = new StringBuffer(); 
			for(int i = 0 ; i < byteData.length ; i++){
				sb.append(Integer.toString((byteData[i]&0xff) + 0x100, 16).substring(1));
			}
			SHA = sb.toString();
		}catch(NoSuchAlgorithmException e){
			e.printStackTrace(); 
			SHA = null; 
		}
		return SHA;
	}
	
	public void profile(MembersDTO dto, MultipartFile ex_file) {
		String uploadPath = session.getServletContext().getRealPath("resources/prof");
		if(!new File(uploadPath).exists()) {
			new File(uploadPath).mkdirs();
		}
		String prof = System.currentTimeMillis() + ".png";
		try {
			ex_file.transferTo(new File(uploadPath + "/" + prof));
			dto.setM_realpath(uploadPath + "/" + prof);
			dto.setM_profile("resources/prof/" + prof);
		} catch (Exception e) {
			e.printStackTrace();
		} 
	}
	
	public int joinSubmit(MembersDTO dto) {
		dto.setM_pw(this.SHA256(dto.getM_pw()));
		dto.setM_ipaddress(request.getRemoteAddr());
		return sst.insert("LoginDAO.joinSubmit", dto);
	}
	
	public String emailDupCheck(String key) {
		Map<String, String> map = new HashMap();
		map.put("col", "m_email");
		map.put("whereCol", "m_email");
		map.put("value", key);
		return sst.selectOne("LoginDAO.dupCheck", map);
	}

	public String phoneDupCheck(String key) {
		Map<String, String> map = new HashMap();
		map.put("col", "m_phone");
		map.put("whereCol", "m_phone");
		map.put("value", key);
		return sst.selectOne("LoginDAO.dupCheck", map);
	}

	public String gardenDupCheck(String key) {
		Map<String, String> map = new HashMap();
		map.put("col", "m_garden");
		map.put("whereCol", "m_garden");
		map.put("value", key);
		return sst.selectOne("LoginDAO.dupCheck", map);
	}
	
	public String isLoginOk(String loginId, String shaPw) {
		Map<String, String> map = new HashMap();
		map.put("id", loginId);
		map.put("pw", shaPw);
		map.put("m_email", "m_email");
		map.put("m_pw", "m_pw");
		return sst.selectOne("LoginDAO.isLoginOk", map);
	}
	
	public String getName(String loginId) {
		Map<String, String> map = new HashMap();
		map.put("value", loginId);
		map.put("col", "m_name");
		map.put("whereCol", "m_email");
		return sst.selectOne("LoginDAO.dupCheck", map);
	}
	
	public MembersDTO memSelectAll(MembersDTO dto, String id) {
		dto.setM_email(id);
		return sst.selectOne("LoginDAO.memSelectAll", dto);
	}

	public String pwDupCheck(String key) {
		Map<String, String> map = new HashMap();
		map.put("col", "m_pw");
		map.put("whereCol", "m_email");
		map.put("value", key);
		return sst.selectOne("LoginDAO.dupCheck", map);
	}
	
	public int delete(String loginId) {
		return sst.delete("LoginDAO.delete", loginId);
	}
	
	public int memUpdateAll(MembersDTO dto) {
		return sst.insert("LoginDAO.memUpdateAll", dto);
	}
	
	public String randomCode() {
		String[] ALPHA = new String[] {"a","b","c","d","e","f","g","h","i","j","k","l","m","n","o","p","q","r","s","t","u","v","w","x","y","z","0","1","2","3","4","5","6","7","8","9"};
		String rN="";
		for(int i=0; i<8; i++){
			int randTnum = (int) Math.floor(Math.random()*ALPHA.length);
			rN += ALPHA[randTnum];
		}
		return rN;
	}
	
	public String mailSender(String m_email) throws Exception {
		// 네이버일 경우 smtp.naver.com 을 입력합니다. 
		// Google일 경우 smtp.gmail.com 을 입력합니다. 
		String host = "smtp.naver.com"; 
		final String username = "sparkss0419"; //네이버 아이디를 입력해주세요. @nave.com은 입력하지 마시구요. 
		final String password = "mygarden55*"; //네이버 이메일 비밀번호를 입력해주세요. 
		int port=465; //포트번호 
		
		// 메일 내용 
		String recipient = m_email; //받는 사람의 메일주소를 입력해주세요. 
		String subject = "나의 정원에서 코드 번호를 보내드립니다"; //메일 제목 입력해주세요. 
			String randomCode = this.randomCode();
		String body = "코드 번호는 " + randomCode + "입니다. "; //메일 내용 입력해주세요. 
		Properties props = System.getProperties(); // 정보를 담기 위한 객체 생성 
		
		// SMTP 서버 정보 설정 
		props.put("mail.smtp.host", host); 
		props.put("mail.smtp.port", port); 
		props.put("mail.smtp.auth", "true"); 
		props.put("mail.smtp.ssl.enable", "true"); 
		props.put("mail.smtp.ssl.trust", host); 
		
		//Session 생성 
		Session session = Session.getDefaultInstance(props, new javax.mail.Authenticator() { 
			String un=username; 
			String pw=password; 
			protected javax.mail.PasswordAuthentication getPasswordAuthentication() { 
				return new javax.mail.PasswordAuthentication(un, pw); 
				} 
			}); 
		session.setDebug(true); //for debug 
		Message mimeMessage = new MimeMessage(session); //MimeMessage 생성 
		mimeMessage.setFrom(new InternetAddress("sparkss0419@naver.com")); //발신자 셋팅 , 보내는 사람의 이메일주소를 한번 더 입력합니다. 이때는 이메일 풀 주소를 다 작성해주세요. 
		mimeMessage.setRecipient(Message.RecipientType.TO, new InternetAddress(recipient)); //수신자셋팅 //.TO 외에 .CC(참조) .BCC(숨은참조) 도 있음
		
		mimeMessage.setSubject(subject); //제목셋팅 
		mimeMessage.setText(body); //내용셋팅 
		Transport.send(mimeMessage); //javax.mail.Transport.send() 이용
		
		return randomCode;
	}
	
	public int findAccountChange(Map<String, String> map) {
		return sst.insert("LoginDAO.updateOne", map);
	}

	public int changeGardenStuff(Map<String, String> map) {
		return sst.insert("LoginDAO.updateOne", map);
	}
	
	public String findId(String key) {
		Map<String, String> map = new HashMap();
		map.put("col", "m_email");
		map.put("whereCol", "m_phone");
		map.put("value", key);
		return sst.selectOne("LoginDAO.dupCheck", map);
	}
	
	public String NaverLoginMakeUrl() {
		try {
		    String clientId = "zoUb6lNYx8sC2suyUmcS";//애플리케이션 클라이언트 아이디값";
		    String redirectURI = URLEncoder.encode("http://localhost/callbackNaver", "UTF-8");
		    SecureRandom random = new SecureRandom();
		    String state = new BigInteger(130, random).toString();
		    String apiURL = "https://nid.naver.com/oauth2.0/authorize?response_type=code";
		    apiURL += "&client_id=" + clientId;
		    apiURL += "&redirect_uri=" + redirectURI;
		    apiURL += "&state=" + state;
		    session.setAttribute("state", state);
		    return apiURL;
		}catch(Exception e) {
			return null;
		}
	}
	
	public String NaverLoginCallback() throws Exception {
		String clientId = "zoUb6lNYx8sC2suyUmcS";//애플리케이션 클라이언트 아이디값";
	    String clientSecret = "bZgqg3cbjr";//애플리케이션 클라이언트 시크릿값";
	    String code = request.getParameter("code");
	    String state = request.getParameter("state");
	    String redirectURI = URLEncoder.encode("http://localhost/callbackNaver", "UTF-8");
	    String apiURL;
	    apiURL = "https://nid.naver.com/oauth2.0/token?grant_type=authorization_code&";
	    apiURL += "client_id=" + clientId;
	    apiURL += "&client_secret=" + clientSecret;
	    apiURL += "&redirect_uri=" + redirectURI;
	    apiURL += "&code=" + code;
	    apiURL += "&state=" + state;
	    String access_token = "";
	    String refresh_token = "";
//	    System.out.println("apiURL="+apiURL);
	    try {
	      URL url = new URL(apiURL);
	      HttpURLConnection con = (HttpURLConnection)url.openConnection();
	      con.setRequestMethod("GET");
	      int responseCode = con.getResponseCode();
	      BufferedReader br;
//	      System.out.print("responseCode="+responseCode);
	      if(responseCode==200) { // 정상 호출
	        br = new BufferedReader(new InputStreamReader(con.getInputStream()));
	      } else {  // 에러 발생
	        br = new BufferedReader(new InputStreamReader(con.getErrorStream()));
	      }
	      String inputLine;
	      StringBuffer res = new StringBuffer();
	      while ((inputLine = br.readLine()) != null) {
//	        System.out.println(inputLine); //access_token json
	        JsonParser parser = new JsonParser();
	        JsonObject jObject = parser.parse(inputLine).getAsJsonObject();
	        String result = jObject.get("access_token").getAsString();
	        return result;
	      }
	      br.close();
	      if(responseCode==200) {
//	        System.out.println(res.toString()); //responseCode
	      }
	    } catch (Exception e) {
	      System.out.println(e);
	    }
	     return null;
	}
	
	public String NaverLoginGetInfo(String token) {
		//String token : 네이버 로그인 접근 토큰;
        String header = "Bearer " + token; // Bearer 다음에 공백 추가
        try {
            String apiURL = "https://openapi.naver.com/v1/nid/me";
            URL url = new URL(apiURL);
            HttpURLConnection con = (HttpURLConnection)url.openConnection();
            con.setRequestMethod("GET");
            con.setRequestProperty("Authorization", header);
            int responseCode = con.getResponseCode();
            BufferedReader br;
            if(responseCode==200) { // 정상 호출
                br = new BufferedReader(new InputStreamReader(con.getInputStream()));
            } else {  // 에러 발생
                br = new BufferedReader(new InputStreamReader(con.getErrorStream()));
            }
            String inputLine;
            StringBuffer response = new StringBuffer();
            while ((inputLine = br.readLine()) != null) {
                response.append(inputLine);
            }
            br.close();
            response.toString(); //사용자 정보 리턴
            JsonParser parser = new JsonParser();
            JsonObject jObject = parser.parse(response.toString()).getAsJsonObject();
            JsonObject infoGroup = jObject.get("response").getAsJsonObject();
            //뽑아낸 정보
            return infoGroup.get("email").getAsString();
        } catch (Exception e) {
            System.out.println(e);
        }
		return null;
	}
	
	public int socialJoinSubmit(MembersDTO dto) {
		return sst.insert("LoginDAO.socialJoinSubmit", dto);
	}
	
	public JsonNode kakaoLoginMakeUrl(String code) {
		final String RequestUrl = "https://kauth.kakao.com/oauth/token"; // Host
        final List<NameValuePair> postParams = new ArrayList<NameValuePair>();
 
        postParams.add(new BasicNameValuePair("grant_type", "authorization_code"));
        postParams.add(new BasicNameValuePair("client_id", "5a8617254e6227196ff9c31a66275c78")); // REST API KEY
        postParams.add(new BasicNameValuePair("redirect_uri", "http://localhost/kakaoCallback")); // 리다이렉트 URI
        postParams.add(new BasicNameValuePair("code", code)); // 로그인 과정중 얻은 code 값
 
        final HttpClient client = HttpClientBuilder.create().build();
        final HttpPost post = new HttpPost(RequestUrl);
 
        JsonNode returnNode = null;
 
        try {
            post.setEntity(new UrlEncodedFormEntity(postParams));
 
            final HttpResponse response = client.execute(post);
            final int responseCode = response.getStatusLine().getStatusCode();
 
//            System.out.println("\nSending 'POST' request to URL : " + RequestUrl);
//            System.out.println("Post parameters : " + postParams);
//            System.out.println("Response Code : " + responseCode);
 
            // JSON 형태 반환값 처리
            ObjectMapper mapper = new ObjectMapper();
            returnNode = mapper.readTree(response.getEntity().getContent());
        } catch (Exception e) {
            e.printStackTrace();
        }
        return returnNode;
	}
	
	public JsonNode kakaoLoginGetInfo(JsonNode accessToken) {
		final String RequestUrl = "https://kapi.kakao.com/v2/user/me";
        final HttpClient client = HttpClientBuilder.create().build();
        final HttpPost post = new HttpPost(RequestUrl);
 
        // add header
        post.addHeader("Authorization", "Bearer " + accessToken);
 
        JsonNode returnNode = null;
 
        try {
            final HttpResponse response = client.execute(post);
            final int responseCode = response.getStatusLine().getStatusCode();
 
//            System.out.println("\nSending 'POST' request to URL : " + RequestUrl);
//            System.out.println("Response Code : " + responseCode);
 
            // JSON 형태 반한값 처리
            ObjectMapper mapper = new ObjectMapper();
            returnNode = mapper.readTree(response.getEntity().getContent());
 
        } catch (Exception e) {
            e.printStackTrace();
        }
        return returnNode;
	}
	
	public Integer[] gardenCalendar(int year) {
		Integer[] calArr = new Integer[12];
        int day = 1;
        int daysOfMonth = 0;
		Calendar cal = Calendar.getInstance();
		for(int month=0; month<12; month++) {
	        cal.set(Calendar.MONTH, 0);
	        int mm=cal.get(Calendar.MONTH)+month;
//	        System.out.println(mm + 1 + "월");
	        Calendar result = new GregorianCalendar(year, mm, day);
	        daysOfMonth = result.getActualMaximum(Calendar.DAY_OF_MONTH);
//	        System.out.println(year + "년 " + (mm+1) + "월의 일수: " + daysOfMonth);
	        calArr[month] = daysOfMonth;
		}
       return calArr;
	}
	
	public List<CalendarDTO> getCalendarList(String loginId) {
	      return sst.selectList("LoginDAO.selectCalendar", loginId);
	   }
	
	public List<ShopListDTO> getOrderlist(ShopListDTO dto){
		if(sst.selectList("LoginDAO.selectOrderList", dto)==null) {
			return null;
		}else {
			return sst.selectList("LoginDAO.selectOrderList", dto);
		}
	}
	
}