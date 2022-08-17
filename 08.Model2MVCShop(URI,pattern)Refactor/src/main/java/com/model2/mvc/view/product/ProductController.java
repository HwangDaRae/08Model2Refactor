package com.model2.mvc.view.product;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.StringTokenizer;
import java.util.UUID;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.commons.fileupload.DiskFileUpload;
import org.apache.commons.fileupload.FileItem;
import org.apache.commons.fileupload.FileUpload;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.model2.mvc.common.Page;
import com.model2.mvc.common.Search;
import com.model2.mvc.common.util.CommonUtil;
import com.model2.mvc.service.domain.Product;
import com.model2.mvc.service.domain.Upload;
import com.model2.mvc.service.domain.Upload_Sub;
import com.model2.mvc.service.domain.User;
import com.model2.mvc.service.product.ProductService;
import com.model2.mvc.service.product.impl.ProductServiceImpl;
import com.model2.mvc.service.upload.UploadService;

@RestController
@RequestMapping("/product/*")
public class ProductController {
	
	@Autowired
	@Qualifier("productServiceImpl")
	ProductService productServiceImpl;
	
	@Autowired
	@Qualifier("uploadServiceImpl")
	UploadService uploadServiceImpl;

	public ProductController() {
		System.out.println(getClass() + " default Constructor()]");
		System.out.println("pageSize : " + pageSize);
		System.out.println("pageUnit : " + pageUnit);
	}
	
	@Value("#{commonProperties['pageUnit']}")
	//@Value("#{commonProperties['pageUnit'] ?: 3}")
	int pageUnit;
	
	@Value("#{commonProperties['pageSize']}")
	//@Value("#{commonProperties['pageSize'] ?: 2}")
	int pageSize;
							  
	@RequestMapping(value = "listProduct/{menu}", method = RequestMethod.GET)
	public Map<String, Object> listProduct( @PathVariable String menu, HttpSession session, Search search) throws Exception {
		System.out.println("/product/listProduct : GET");
		System.out.println(search);
		System.out.println(menu);
		System.out.println(session.getAttribute("user"));
		
		if(((User)session.getAttribute("user")).getUserId().equals("non-member")) {
			//��ȸ�� ��ǰ �˻� Anchor Tag Ŭ��
			System.out.println("��ȸ������ ���Դ�");
		}else if(((User)session.getAttribute("user")).getRole().equals("admin")) {
			System.out.println("admin�������� ���Դ�");
		}else {
			System.out.println("user�������� ���Դ�");
		}

		// ��ǰ�˻� Ŭ�������� currentPage�� null�̴�
		int currentPage = 1;

		// ��ǰ�˻� Ŭ���� null, �˻���ư Ŭ���� nullString
		if (search.getCurrentPage() != 0) {
			currentPage = search.getCurrentPage();
		}

		// �ǸŻ�ǰ���� Ŭ���� searchKeyword, searchCondition �� �� null ==> nullString ���� ��ȯ
		String searchKeyword = CommonUtil.null2str(search.getSearchKeyword());
		String searchCondition = CommonUtil.null2str(search.getSearchCondition());
		
		// ��ǰ��� ��ǰ���ݿ��� searchKeyword�� �����϶� nullString���� ��ȯ
		if (!searchCondition.trim().equals("1") && !CommonUtil.parsingCheck(searchKeyword)) {
			searchKeyword = "";
		}
		search = new Search(currentPage, searchCondition, searchKeyword, pageSize, search.getPriceSort());
		
		// �˻������� �־ ���� �������� list�� �����´�
		List<Product> prodList = productServiceImpl.getProductList(search);		
		int totalCount = productServiceImpl.getProductTotalCount(search);		
		Page resultPage = new Page(currentPage, totalCount, pageUnit, pageSize);
		
		for (int i = 0; i < prodList.size(); i++) {
			System.out.println(getClass() + " : " + prodList.get(i).toString());
		}
		
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("resultPage", resultPage);
		map.put("searchVO", search);
		map.put("list", prodList);
		map.put("listSize", prodList.size());
		map.put("menu", menu);
		
		return map;
	}
	
	@RequestMapping( value = "listProduct", method = RequestMethod.POST )
	public Map<String, Object> listProduct( @RequestBody String menu, User user, HttpSession session, Search search) throws Exception {
		System.out.println("/product/listProduct : POST");
		System.out.println(search);
		System.out.println(user);
		System.out.println(menu);
		System.out.println(session.getAttribute("user"));
		
		if(((User)session.getAttribute("user")).getUserId().equals("non-member")) {
			//��ȸ�� ��ǰ �˻� Anchor Tag Ŭ��
			System.out.println("��ȸ������ ���Դ�");
		}else if(((User)session.getAttribute("user")).getRole().equals("admin")) {
			System.out.println("admin�������� ���Դ�");
		}else {
			System.out.println("user�������� ���Դ�");
		}

		// ��ǰ�˻� Ŭ�������� currentPage�� null�̴�
		int currentPage = 1;

		// ��ǰ�˻� Ŭ���� null, �˻���ư Ŭ���� nullString
		if (search.getCurrentPage() != 0) {
			currentPage = search.getCurrentPage();
		}

		// �ǸŻ�ǰ���� Ŭ���� searchKeyword, searchCondition �� �� null ==> nullString ���� ��ȯ
		String searchKeyword = CommonUtil.null2str(search.getSearchKeyword());
		String searchCondition = CommonUtil.null2str(search.getSearchCondition());
		
		// ��ǰ��� ��ǰ���ݿ��� searchKeyword�� �����϶� nullString���� ��ȯ
		if (!searchCondition.trim().equals("1") && !CommonUtil.parsingCheck(searchKeyword)) {
			searchKeyword = "";
		}
		search = new Search(currentPage, searchCondition, searchKeyword, pageSize, search.getPriceSort());
		
		// �˻������� �־ ���� �������� list�� �����´�
		List<Product> prodList = productServiceImpl.getProductList(search);		
		int totalCount = productServiceImpl.getProductTotalCount(search);		
		Page resultPage = new Page(currentPage, totalCount, pageUnit, pageSize);
		
		for (int i = 0; i < prodList.size(); i++) {
			System.out.println(getClass() + " : " + prodList.get(i).toString());
		}
		
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("resultPage", resultPage);
		map.put("searchVO", search);
		map.put("list", prodList);
		map.put("listSize", prodList.size());
		map.put("menu", menu);
		
		return map;
	}
	
	@RequestMapping( value = "getProduct/{prodNo}/{menu}", method = RequestMethod.GET )
	public Product getProduct(@PathVariable int prodNo, @PathVariable String menu ) throws Exception {
		System.out.println("/getProduct : GET");
		return productServiceImpl.getProduct(prodNo);
	}
	
	@RequestMapping( value = "addProductView", method = RequestMethod.GET )
	public String addProductView() throws Exception {
		System.out.println("/addProductView : GET");
		return "redirect:/product/addProductView.jsp";
	}

	@RequestMapping(value = "addProduct", method = RequestMethod.POST)
	public String addProduct(@ModelAttribute Product productVO,
							@RequestParam("uploadfile") List<MultipartFile> multiFileList,
							HttpServletRequest request,
							Upload uploadVO,
							ArrayList<Upload> uploadList ) throws Exception {
		System.out.println("/product/addProduct : POST");
		
		//������ȣ ����
		SimpleDateFormat sdf1 = new SimpleDateFormat("yyyyMMddHHmmss");
		String fileNo = sdf1.format( Calendar.getInstance().getTime() ) + "";
		
		for(int i = 0; i < multiFileList.size(); i++) {
			multiFileList.get(i).transferTo(new File("C:\\Users\\bitcamp\\git\\08Refactor\\08.Model2MVCShop(URI,pattern)Refactor\\src\\main\\webapp\\images\\uploadFiles\\",
					multiFileList.get(i).getOriginalFilename()));
			uploadVO = new Upload();
			uploadVO.setFileNo(fileNo);
			uploadVO.setFileCount(multiFileList.size());
			uploadVO.setFileName(multiFileList.get(i).getOriginalFilename());
			uploadVO.setFile_path("C:\\Users\\bitcamp\\git\\08Refactor\\08.Model2MVCShop(URI,pattern)Refactor\\src\\main\\webapp\\images\\uploadFiles");
			uploadList.add(uploadVO);
		}
		
		productVO.setFileName(fileNo);		
		productServiceImpl.addProduct(productVO);
		for (Upload upload : uploadList) {
			uploadServiceImpl.addUpload(upload);
		}
		
		request.setAttribute("productVO", productVO);
		request.setAttribute("uploadList", uploadList);
		request.setAttribute("count", uploadVO.getFileCount());
		
		return "forward:/product/addProduct.jsp";
	}
	
	@RequestMapping(value = "updateProductView/{prodNo}/{menu}", method = RequestMethod.GET )
	public Map<String, Object> updateProductView(@PathVariable int prodNo, @PathVariable String menu) throws Exception {
		System.out.println("/product/updateProductView : GET");
		
		Product productVO = productServiceImpl.getProduct(prodNo);
		List<Upload> uploadList = uploadServiceImpl.getUploadFile(productVO.getFileName());
		
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("productVO", productVO);
		map.put("uploadList", uploadList);
		map.put("count", uploadList.size());
		
		if( menu.equals("manage") && productVO.getProTranCode() == null ) {
			return map;
		}else {
			return map;
		}
	}
	
	@RequestMapping(value = "updateProduct", method = RequestMethod.POST )
	public String updateProduct(@ModelAttribute("productVO") Product productVO,
								@RequestParam("uploadfile") List<MultipartFile> multiFileList,
								HttpServletRequest request,
								Upload uploadVO,
								ArrayList<Upload> uploadList ) throws Exception {
		System.out.println("/product/updateProduct : POST");
		
		// File file = new File(��� + �����̸�);
		for(int i = 0; i < multiFileList.size(); i++) {
			multiFileList.get(i).transferTo(new File("C:\\Users\\bitcamp\\git\\08Refactor\\08.Model2MVCShop(URI,pattern)Refactor\\src\\main\\webapp\\images\\uploadFiles\\",
					multiFileList.get(i).getOriginalFilename()));
			uploadVO = new Upload();
			uploadVO.setFileName(multiFileList.get(i).getOriginalFilename());
			uploadVO.setFileNo(productVO.getFileName());
			uploadVO.setFileCount(multiFileList.size());
			uploadVO.setFile_path("C:\\Users\\bitcamp\\git\\08Refactor\\08.Model2MVCShop(URI,pattern)Refactor\\src\\main\\webapp\\images\\uploadFiles\\");
			List<Upload> list = uploadServiceImpl.getUploadFile(productVO.getFileName());
			uploadVO.setBefore_fileName(list.get(i).getFileName());
			uploadList.add(uploadVO);
		}
		
		productVO = productServiceImpl.updateProduct(productVO);
		for (Upload upload : uploadList) {
			uploadServiceImpl.updateUpload(upload);
		}
		List<Upload> list = uploadServiceImpl.getUploadFile(uploadList.get(0).getFileNo());
		
		request.setAttribute("productVO", productVO);
		request.setAttribute("uploadList", list);
		request.setAttribute("count", uploadVO.getFileCount());
		
		return "forward:/product/getProduct.jsp";
	}

}


