package com.qa.tests;

import com.qa.BaseTest;
import com.qa.pages.LoginPage;
import com.qa.pages.ProductDetailsPage;
import com.qa.pages.ProductsPage;
import com.qa.pages.SettingsPage;
import org.json.JSONObject;
import org.json.JSONTokener;
import org.testng.Assert;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;
import org.testng.asserts.SoftAssert;

import java.io.InputStream;
import java.lang.reflect.Method;

public class ProductTest extends BaseTest{
	LoginPage loginPage;
	ProductsPage productsPage;
	ProductDetailsPage productDetailsPage;
	SettingsPage settingsPage;
	JSONObject loginUsers;
//	TestUtils utils = new TestUtils();
	
	  @BeforeClass
	  public void beforeClass() throws Exception {
		  InputStream datais = null;

		  try {
			  String dataFileName = "data/loginUsers.json";
			  datais = getClass().getClassLoader().getResourceAsStream(dataFileName);
			  JSONTokener tokener = new JSONTokener(datais);
			  loginUsers = new JSONObject(tokener);
		  } catch(Exception e) {
			  e.printStackTrace();
			  throw e;
		  } finally {
			  if(datais != null) {
				  datais.close();
			  }
		  }
		  closeApp();
		  launchApp();
	  }

	  @BeforeMethod
	  public void beforeMethod(Method m) {
		  System.out.print("\n" + "****** starting test:" + m.getName() + "******" + "\n");
		  loginPage = new LoginPage();
		  productsPage = loginPage.login(loginUsers.getJSONObject("validUser").getString("username"),
				  loginUsers.getJSONObject("validUser").getString("password"));
	  }
	@AfterMethod
	public void afterMethod(Method m) {
		settingsPage = productsPage.pressSettingsBtn();
		loginPage = settingsPage.pressLogoutBtn();
	}
	@Test
	  public void validateProductOnProductsPage() {
	  	SoftAssert softAssert  = new SoftAssert();

		String SLBTitle = productsPage.getSLBTitle();
		softAssert.assertEquals(SLBTitle, getStrings().get("products_page_slb_title"));
		 String SLBPrice = productsPage.getSLBPrice();
		softAssert.assertEquals(SLBPrice, getStrings().get("products_page_slb_price"));

		softAssert.assertAll();
	  }

	  @Test
	  public void validateProductOnProductDetailsPage() {
		  SoftAssert softAssert  = new SoftAssert();

		  productDetailsPage = productsPage.pressSLBTitle();
		  String SLBTitle = productDetailsPage.getSLBTitle();
		  softAssert.assertEquals(SLBTitle, getStrings().get("product_details_page_slb_title"));
		  String SLBText = productDetailsPage.getSLBTxt();
		  softAssert.assertEquals(SLBText, getStrings().get("product_details_page_slb_txt"));

//
//		  if(getPlatform().equalsIgnoreCase("Android")) {
//		  productDetailsPage.scrollToSLBPriceAndGetSLBPrice();
//			  String SLBPrice = productDetailsPage.getSLBPrice();
//			  softAssert.assertEquals(SLBPrice, strings.get("product_details_page_slb_price"));
//		  }
//		  if(getPlatform().equalsIgnoreCase("iOS")) {
//			  String SLBTxt = productDetailsPage.getSLBTxt();
//			  softAssert.assertEquals(SLBTxt, strings.get("product_details_page_slb_txt"));

			  productDetailsPage.scrollPage();
			  softAssert.assertTrue(productDetailsPage.isAddToCartBtnDisplayed());
//		  }

		  productsPage = productDetailsPage.pressBackToProductsBtn();

		  softAssert.assertAll();
	  }
}

