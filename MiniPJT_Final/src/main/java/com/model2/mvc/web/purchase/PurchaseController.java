package com.model2.mvc.web.purchase;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;

import com.model2.mvc.common.Page;
import com.model2.mvc.common.Search;
import com.model2.mvc.service.domain.Product;
import com.model2.mvc.service.domain.Purchase;
import com.model2.mvc.service.domain.User;
import com.model2.mvc.service.product.ProductService;
import com.model2.mvc.service.purchase.PurchaseService;

@Controller
@RequestMapping("/purchase/*")
public class PurchaseController {
	
	@Autowired
	@Qualifier("purchaseService")
	private PurchaseService purchaseService;
	
	@Autowired
	@Qualifier("productService")
	private ProductService productService;
	
	@Value("#{commonProperties['pageUnit']}")
	int pageUnit;
	
	@Value("#{commonProperties['pageSize']}")
	int pageSize;
	
	public PurchaseController() {
		// TODO Auto-generated constructor stub
		System.out.println(this.getClass());
	}
	
//	@RequestMapping("/addPurchase.do")
	@RequestMapping("addPurchase")
	public String addPurchase(	@ModelAttribute("purchase") Purchase purchase,
								@ModelAttribute("product") Product product,
								HttpSession session) throws Exception{
		
		purchase.setBuyer((User)session.getAttribute("user"));
		
		int prodTotal = productService.getProduct(product.getProdNo()).getProdTotal();
		prodTotal -= purchase.getPurchaseQuantity();
		product.setProdTotal(prodTotal);
		productService.updateProduct2(product);
		
		purchase.setPurchaseProd(product);
		purchase.setTranCode("1");
		System.out.println(purchase);
		
		purchaseService.addPurchase(purchase);
		
		return "forward:/purchase/addPurchase.jsp";
	}
	
//	@RequestMapping("/addPurchaseView.do")
	@RequestMapping("addPurchaseView")
	public String addPurchaseView(	@RequestParam("prodNo") String prodNo,
									Model model) throws Exception{
		
		model.addAttribute("product",productService.getProduct(Integer.parseInt(prodNo)));
		
		return "forward:/purchase/addPurchaseView.jsp";
	}
	
	@RequestMapping("getPurchase")
	public String getPurchase(	@ModelAttribute("product") Product product,
								Model model, HttpSession session) throws Exception{
		
		int prodNo = product.getProdNo();
		int tranNo = product.getTranNo();
		
		Purchase purchase = null;
		List<Purchase> list = null;
		if(prodNo != 0 ) {
			list = purchaseService.getPurchase2(prodNo);
			model.addAttribute("list",list);
		}
		if(tranNo != 0) {
			purchase = purchaseService.getPurchase(tranNo);
			model.addAttribute("purchase",purchase);
		}
		
		model.addAttribute("user",(User)session.getAttribute("user"));
		
		return "forward:/purchase/getPurchase.jsp";
	}
	
//	@RequestMapping("/listPurchase.do")
	@RequestMapping("listPurchase")
	public String listPurchase(	@ModelAttribute("search") Search search,
								HttpSession session, Model model) throws Exception{
		
		//���� ���µ� ������ ��ȣ
		if(search.getCurrentPage() == null || search.getCurrentPage().equals("")){
			search.setCurrentPage("1");
		}else {
			String[] currentPage =search.getCurrentPage().split(",");
			search.setCurrentPage(currentPage[0]);
		}
		
		
		User user = (User)session.getAttribute("user");
		
		String[] sort= null;
		String rowCondition = search.getRowCondition();
		if(rowCondition != null && !rowCondition.equals("")) {
			sort = rowCondition.split(",");
			
			if(sort[0].equals("3���� ����")) {
				pageSize = 3;
			}else if(sort[0].equals("5���� ����")) {
				pageSize = 5;
			}else if(sort[0].equals("10���� ����")) {
				pageSize = 10;
			}
		}
		search.setPageSize(pageSize);
		
		String sortCondition = search.getSortCondition();
		if(sortCondition != null && !sortCondition.equals("")) {
			sort = sortCondition.split(",");
			search.setSortCondition(sort[0]);
		}
		
		Map<String , Object> map= purchaseService.getPurchaseList(search, user.getUserId());
		
		Page resultPage = new Page( search.getCurrentPage(), ((Integer)map.get("totalCount")).intValue(), pageUnit, pageSize);
		
		model.addAttribute("resultPage", resultPage);
		model.addAttribute("list", map.get("list"));
		model.addAttribute("search", search);
		
		return "forward:/purchase/listPurchase.jsp";
	}
	
//	@RequestMapping("/updatePurchase.do")
	@RequestMapping("updatePurchase")
	public String updatePurchase(	@ModelAttribute("purchase") Purchase purchase,
									@ModelAttribute("product") Product product,
									@ModelAttribute("user") User user) throws Exception{
		
		purchase.setPurchaseProd(product);
		purchase.setBuyer(user);
		purchaseService.updatePurchase(purchase);
		purchase.setOrderDate(LocalDate.now().toString());
		return "forward:/purchase/updatePurchase.jsp";
	}
	
	@RequestMapping("updatePurchaseView")
	public String updatePurchaseView(	@RequestParam("tranNo") int tranNo,
										Model model) throws Exception{
		
		model.addAttribute("purchase",purchaseService.getPurchase(tranNo));
		
		return "forward:/purchase/updatePurchaseView.jsp";
	}
//	@RequestMapping("/updateTranCode.do")
	@RequestMapping("updateTranCode")
	public String updateTranCode(	@ModelAttribute("purchase") Purchase purchase,
									@ModelAttribute("product") Product product,
									@ModelAttribute("search") Search search,
									HttpSession session,
									Model model) throws Exception{
		User user = (User)session.getAttribute("user");
		purchase.setTranCode("3");
		purchase.setPurchaseProd(product);
		System.out.println(purchase);
		
		purchaseService.updateTranCode(purchase);
		search.setPageSize(pageSize);
		
		Map<String , Object> map= purchaseService.getPurchaseList(search, user.getUserId());
		
		Page resultPage = new Page( search.getCurrentPage(), ((Integer)map.get("totalCount")).intValue(), pageUnit, pageSize);
		
		model.addAttribute("resultPage", resultPage);
		model.addAttribute("list", map.get("list"));
		
		return "forward:/purchase/listPurchase.jsp";
	}
//	@RequestMapping("/updateTranCodeByProd.do")
	@RequestMapping("updateTranCodeByProd")
	public String updateTranCodeByProd(	@ModelAttribute("purchase") Purchase purchase,
										@ModelAttribute("search") Search search,
										Model model) throws Exception{
		
		purchase.setTranCode("2");
		purchaseService.updateTranCode(purchase);
		
		search.setSearchCondition(null);
		search.setPageSize(pageSize);
		Map<String , Object> map = productService.getProductListAdmin(search);
		
		Page resultPage = new Page( search.getCurrentPage(), ((Integer)map.get("totalCount")).intValue(), pageUnit, pageSize);
		
		model.addAttribute("resultPage", resultPage);
		model.addAttribute("list", map.get("list"));
		
	
		return "forward:/product/listProduct.jsp";
	}
	//============================================��ٱ���======================================
	@RequestMapping("addPurchaseCart")
	public ModelAndView addPurchaseCart(HttpSession session) throws Exception {
		
		Purchase purchase = new Purchase();
		
		Product product = (Product)session.getAttribute("product");
		User user = (User)session.getAttribute("user");
		purchase.setPurchaseProd(product);
		purchase.setBuyer(user);
		
		if(purchaseService.findCart(purchase) == null) {
			
			purchaseService.addPurchaseCart(purchase);				
		}
		
		ModelAndView mav = new ModelAndView();
		
		mav.setViewName("forward:/product/getProduct.jsp");
		mav.addObject("product",product);
		
		return mav;
	}
	
	@RequestMapping("findCartList")
	public ModelAndView findCartList( @ModelAttribute("search") Search search, HttpSession session) throws Exception {
		
		if(search.getCurrentPage() == null || search.getCurrentPage().equals("")){
			search.setCurrentPage("1");
		}else {
			String[] currentPage =search.getCurrentPage().split(",");
			search.setCurrentPage(currentPage[0]);
		}
		
		String[] sort= null;
		String rowCondition = search.getRowCondition();
		if(rowCondition != null && !rowCondition.equals("")) {
			sort = rowCondition.split(",");
			
			if(sort[0].equals("3���� ����")) {
				pageSize = 3;
			}else if(sort[0].equals("5���� ����")) {
				pageSize = 5;
			}else if(sort[0].equals("10���� ����")) {
				pageSize = 10;
			}
		}
		search.setPageSize(pageSize);
		
		User user = (User)session.getAttribute("user");
		
		Map<String , Object> map= purchaseService.findCartList(search, user.getUserId());
		
		Page resultPage = new Page( search.getCurrentPage(), ((Integer)map.get("total")).intValue(), pageUnit, pageSize);
		
		ModelAndView mav = new ModelAndView();
		
		mav.setViewName("forward:/purchase/listCart.jsp");
		mav.addObject("resultPage",resultPage);
		mav.addObject("list",map.get("list"));
	
		return mav;
	}
	
	@RequestMapping("addCartListView")
	public ModelAndView addCartListView(@ModelAttribute("product") Product product,
									HttpSession session) throws Exception{
		
		ModelAndView mav = new ModelAndView();
		
		String[] prodNoList = product.getProdNoList();
		String[] totalList = product.getProdTotalList();
		String prodNo = "";
		String prodName = "";
		String prodTotal = "";
		String total = "";
		
		
		for(int i=0; i < prodNoList.length; i++) {
			prodNo = prodNoList[i];
			prodTotal =totalList[i];
			if(prodNo != null && !prodNo.equals("")) {
				prodName += "["+prodNo+"]"+(productService.getProduct(Integer.parseInt(prodNo))).getProdName();
				total += (productService.getProduct(Integer.parseInt(prodNo))).getProdName() +" : "+prodTotal+"��";
			}
			if(i < prodNoList.length-1) {
				prodName += " / ";
				total += " / ";
			}
		}
		
		mav.setViewName("forward:/purchase/addPurchaseView.jsp");
		mav.addObject("user",(User)session.getAttribute("user"));
		mav.addObject("totals",total);
		mav.addObject("prodName",prodName);	
		
		return mav;
	}
	
	@RequestMapping ("addCartList")
	public ModelAndView addCartList(	@ModelAttribute("purchase") Purchase purchase,
										@RequestParam("prodTotal") String prodTotal,
										@RequestParam("prodName") String prodName,
										HttpSession session) throws Exception{
		
		User user = (User)session.getAttribute("user");
		
		ModelAndView mav = new ModelAndView();
		
		String[] prodTotals = prodTotal.split(" / ") ;
		String[] prodNames = prodName.split(" / ") ;
		
		int prodNo = 0;
		int length = 0;
		int purchaseQuantity = 0;
		String name = "";
		String temp = "";
		
		Product product = null;
		
		for(int i = 0; i<prodTotals.length; i++) {
			
			length = prodNames[i].length();
			name = prodNames[i].substring(7,length);
			
			temp = prodTotals[i].replace(name+" : ", "");
			purchaseQuantity = Integer.parseInt(temp.replace("��", ""));
			
			temp = prodNames[i].replace(name, "");
			prodNo = Integer.parseInt(temp.substring(1,6));
			
			product = productService.getProduct(prodNo);
			
			product.setProdTotal(product.getProdTotal()-purchaseQuantity);
			
			productService.updateProduct2(product);
			
			purchase.setPurchaseProd(product);
			purchase.setTranCode("1");
			purchase.setPurchaseQuantity(purchaseQuantity);
			purchase.setBuyer(user);
			
			purchaseService.addPurchase(purchase);
			
			purchaseService.deleteCart(user.getUserId(), prodNo);
			
		}
		
		mav.setViewName("forward:/purchase/addPurchase.jsp");
		mav.addObject("purchase",purchase);
		mav.addObject("prodName", prodName);
		mav.addObject("prodTotal",prodTotal);
		
		return mav;
	}
}