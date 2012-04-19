package controllers;

import java.util.*;

import play.*;
import play.mvc.*;
import play.utils.HTML;
import controllers.Secure;
import models.*;

import play.data.validation.Error;
import javax.imageio.ImageIO;
import java.io.*;

@With(Secure.class)
public class Pages extends OBController {
	
	public static void newPage(){
		User user = user();
		render(user);
	}
	
	public static void pageSave(String title, String info){
		User user = user();
		User currentUser = user();
		Page page = new Page(user, title, info).save();
		new UserPage(user, page).save();
		//render("Pages/myPage.html", page,user, currentUser);
		display(page.id);
	}
	

  public static void pageUpdate(Long id, String info){
  	Page page = Page.findById(id); 
  	page.info = info;
    page.save();
    //User _currentUser = user();
    User _user = user();
    display(id);
    //render("Pages/myPage.html", page, _user);
  }
	                                                                     
	public static void myPages(){
		User _user = user(); 
		List<UserPage> myPages = UserPage.find("select u from UserPage u where u.fan = ?", _user).fetch();
		if(myPages == null){renderText("null");}
		render(myPages, _user);
	}
	
	public static void display(Long id){
		User _user = user();
		User _currentUser = user();
		Page page = Page.findById(id);
		boolean fan = isFan(id);
		List<UserPage> myPages = UserPage.find("select u from UserPage u where u.fan = ?", _user).fetch();
		List<Photo> photos = PGEphotos.pagePhotos(id, 4);
		render("Pages/myPage.html", page, fan, _user, _currentUser, photos);
	}

	public static void pages(){
		User user = user();
		List<Page> allPages = Page.findAll();
		render(allPages,user);
	}
	
	public static void deletePage(Long id){
		User user = user();
		Page page = Page.findById(id);
		UserPage link = UserPage.find("select u from UserPage u where u.page = ?", page).first();
		link.delete();
		page.delete();
		render(user);
	}
	
	public static void unfan(String pid){
		User user = user();
		Page page = Page.findById(Long.parseLong(HTML.htmlEscape(pid)));
		UserPage u = UserPage.find("select u from UserPage u where u.page = ?", page).first();
    u.delete();
		Map<String, Object> m = new HashMap<String, Object>();
    m.put("fan",false);
    renderJSON(m);
	}
	
	public static void fan(String pid){
		User user = user();
		Page page = Page.findById(Long.parseLong(HTML.htmlEscape(pid)));
		final UserPage u = new UserPage(user, page).save();
    Map<String, Object> m = new HashMap<String, Object>();
    m.put("fan",true);
    renderJSON(m);
	}
	
	public static boolean isFan(Long id){
		User user = user();
		UserPage u = UserPage.find("select u from UserPage u where u.page.id = ? and u.fan = ?", id, user).first();
		if(u == null){return false;}
		else{return true;}
	}
	
}
