package hello;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.client.discovery.DiscoveryClient;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

@RestController
public class EurekaClientController {
	
	@Autowired
	EurekaClientServiceImpl service = new EurekaClientServiceImpl();

    @RequestMapping("/")
    public String index() {
        return "<p>Please use below api for scan and return the ocr data</p>\r\n" + 
        		"<p>For swagger please refer UI : <a href=\"http://localhost:8080/swagger-ui.html\">http://localhost:8080/swagger-ui.html</a></p>\r\n" + 
        		"<ul>\r\n" + 
        		"  <li><b>Controller </b> : Eureka Client Controller</li>\r\n" + 
        		"  <li><b>API </b>: /scanfile (ex: http://localhost:8080/scanfile )</li>\r\n" + 
        		"  <li><b>params </b>: MultipartFile file (jpg or png or pdf)</li>\r\n" + 
        		"</ul>";
    }
    
    @PostMapping("/scanfile")
    public List<String> scanData(@RequestParam("file") MultipartFile file) {
    	List<String> result = new ArrayList<String>();
    	try {
    		result = service.uploadAndScanFile(file);
    	}
    	catch (Exception e) {
			throw new RuntimeException("Could not scan the file data: " + e.getMessage());
		}
    	return result;
    }
}
