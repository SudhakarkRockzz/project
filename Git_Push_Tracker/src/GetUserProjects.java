package main.GitAutomation;

import java.io.IOException;
import java.net.URI;
import java.time.Duration;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.LocalDate;
import java.time.Instant;
import java.time.ZoneId;
import java.util.ArrayList;
import org.json.JSONArray;
import org.json.JSONObject;
import com.google.gson.*;

public class GetUserProjects {
	
	private int contribution_count = 0;
	private int userid; 

	private final String baseUrl = "https://gitlab.com/api/v4/projects/?owned=true";
	
    private static final HttpClient CLIENT = HttpClient.newBuilder()
            .version(HttpClient.Version.HTTP_2)
            .connectTimeout(Duration.ofSeconds(10))
            .build();
	
    public void getUserId(String access_token, String username) throws IOException, InterruptedException {
    	String userIdUrl = "https://gitlab.com/api/v4/users?username="+ username;
    	HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(userIdUrl))
                .header("Authorization", "Bearer " + access_token)
                .header("Accept", "application/json")
                .GET()
                .build();
		
        HttpResponse<String> response = CLIENT.send(request, HttpResponse.BodyHandlers.ofString());
        
        String responseBody = response.body();
        
        JsonArray projects = JsonParser.parseString(responseBody).getAsJsonArray();
           
        projects.forEach(element ->{
        	JsonObject userDetails = element.getAsJsonObject();
        	this.userid = userDetails.get("id").getAsInt();
        });	
    }
    
   
	public void getEvents(String username, String access_token) throws IOException, InterruptedException {
		String eventsUrl = "https://gitlab.com/api/v4/users/"+username+"/events";
		
//		System.out.println("Current Url: "+eventsUrl);
		
		HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(eventsUrl))
                .header("Authorization", "Bearer " + access_token)
                .header("Accept", "application/json")
                .GET()
                .build();
		
        HttpResponse<String> response = CLIENT.send(request, HttpResponse.BodyHandlers.ofString());
        
        
        if(response.statusCode() != 200) {
        	System.out.println("Connection failed....");
        	System.out.println(response.body());
        	return;
        }
        
        String responseBody = response.body();
        
        JsonArray events= JsonParser.parseString(responseBody).getAsJsonArray();
              
        events.forEach(element -> {
            JsonObject obj = element.getAsJsonObject();
            
            String status = obj.get("action_name").getAsString();
            
//            System.out.println("username : "+username+" status: "+status);
//            
//            System.out.println();
            
            
            if(!status.equals("pushed to")) {
            	return;
            }          
            
            String created_at = obj.get("created_at").getAsString();
            
            
            if(isToday(created_at)) {
            	
            	this.contribution_count++;
            	
        	}
        });        
		
	}
	
	
	public boolean isToday(String dateString) {
        Instant instant = Instant.parse(dateString);
        LocalDate parsedDate = instant.atZone(ZoneId.systemDefault()).toLocalDate();
        LocalDate today = LocalDate.now();
        if (parsedDate.isEqual(today)) {
            return true;
        }else {
        	return false;
        }
	}
	

	public static void main(String[] args) throws IOException, InterruptedException {
		
		System.out.println("+-----------------+");
		System.out.println("| Program Started |");
		System.out.println("+-----------------+");
		
		String UrlMessageString = "";
		GetUserProjects gitTrack1;
		
		ExcelSample excel = new ExcelSample();
//		
		int rowNum = 0, colIndex = 0;

		// write the headers
		
//		excel.write(rowNum, colIndex++, "Gitlab Username");
//		excel.write(rowNum, colIndex++, "Total Contributions");
//		
//		colIndex = 0; // reset the column index
//		rowNum++;
		
		ArrayList<ArrayList> array =  excel.readFile();
		
		if(array == null) {
			System.out.println("File not found");
			return;
		}
		
		if(array.size() == 0) {
			System.out.println("NO contents in File");
			return;
		}
		
		TelegramIntegration telegram = new TelegramIntegration();
		
		for(int i=0; i<array.size();i++) {
			gitTrack1 = new GetUserProjects();
			ArrayList<String> innerArray = array.get(i);
			System.out.println(gitTrack1);
			
			// get the access_token, username [[324234, username],]
			
			String acess_token = innerArray.get(0);
			String username = innerArray.get(1);
			
			// get User id from the username
			try {
				gitTrack1.getUserId(acess_token, username);	
			}
			catch(Exception e) {
				rowNum++;
				continue;
			}
			
			
			// get the contribution count
//			gitTrack1.getProjects(acess_token);
			gitTrack1.getEvents(username, acess_token);
//			System.out.println("Push count for "+username+": "+gitTrack1.contribution_count);
			// writing to the excel (username) 
//			XSSFRow row =  excel.createRow(rowNum);
//			excel.write(rowNum, colIndex++, username);
			
			
			String value;
			if(gitTrack1.contribution_count>0) {
				value = gitTrack1.contribution_count+"+Contributions";
			}
			else {
				value = "No+Contributions";
			}
			
			UrlMessageString = UrlMessageString+username+"+"+value+"%0A";
				
						
			// writing to the excel (contribution count)
//			excel.write(rowNum, colIndex, value);
			
//			rowNum++; // increase row count.
//			colIndex = 0; // reset column count.
//			excel.saveFile();  // finally save this file in filesystem.
			
//			System.out.println("Count Complete for: "+ gitTrack1.userid);

		}
		
		System.out.println(UrlMessageString);
		
//		telegram.sendMessage(UrlMessageString);
		
		System.out.println("+------------------+");
		System.out.println("| Program Finished |");
		System.out.println("+------------------+");
		
	}

}

