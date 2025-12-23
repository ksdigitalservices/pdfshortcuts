package com.pdf.parsing.home;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

import ch.qos.logback.core.model.Model;

@Controller
public class HomeController {
	

	private static final Logger log = LoggerFactory.getLogger(HomeController.class);

	@GetMapping("/")
	public String homePage() {
		return "home";
	}
	@GetMapping("/arogya")
	public String home() {
		return "arogya";
	}
	@GetMapping("/arogya1")
	public String home1() {
		return "arogya1";
	}
	@GetMapping("/card")
	public String card() {
		return "card";
	}
	@GetMapping("/arogyaDownload")
	public String arogyaDownload() {
		return "arogyaDownload";
	}
	@GetMapping("/back")
	public String back() {
		return "arogyaBack";
	}
	@GetMapping("/google0062e0de736cb797.html")
	public String crawl()
	{
		return "google0062e0de736cb797";
	}
	
	  @GetMapping("/robots.txt")
	  public String page()
	  {
		  return "robots.txt";
	  }

	@GetMapping("/terms")
	public String terms() {
		return "terms";
	}

	@GetMapping("/privacy")
	public String privacy() {
		return "privacy";
	}

	@GetMapping("/cancellation")
	public String cancellation() {
		return "cancellation";
	}

	@GetMapping("/contact")
	public String contact() {
		return "contact";
	}

	@GetMapping("/about")
	public String aboutUs() {
		return "about";
	}

	@GetMapping("/shipping")
	public String shippig() {
		return "shipping";
	}

	@GetMapping("/foodLicense")
	public String foodLicense()
	{
		return "foodLicense";
	}
	@GetMapping("/tradeLicense")
	public String tradeLicense()
	{
		return "tradeLicense";
	}
	@GetMapping("/labourLicense")
	public String labourLicense()
	{
		return "labourLicense";
	}
	@GetMapping("/msme")
	public String msme()
	{
		return "msme";
	}
	@GetMapping("/passport")
	public String passport()
	{
		return "passport";
	}
	@GetMapping("/pancard")
	public String pancard()
	{
		return "pancard";
	}
	@GetMapping("/marriage")
	public String marriage()
	{
		return "marriage";
	}
	@GetMapping("/birth")
	public String birth()
	{
		return "birth";
	}
	@GetMapping("/details")
	public String details()
	{
		return "multipleLinks";
	}
	
	@GetMapping("/shortcut")
    public String photo()
    {
    	return "shortcut";
    }

    @GetMapping("/passport_photo")
    public String index(Model model) {
        return "photo";
    }
    @GetMapping("/autofill")
    public String autofill() {
        return "autofill";
    }
    

    
	 
}