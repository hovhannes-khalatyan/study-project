package com.hovo.shop;

import com.hovo.shop.model.Category;
import com.hovo.shop.model.Product;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.*;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.util.UriComponentsBuilder;

import java.util.HashSet;
import java.util.Set;

import static com.hovo.shop.model.Product.OnlineStatus.ACTIVE;
import static com.hovo.shop.model.Product.OnlineStatus.BLOCKED;
import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest(classes = ShopApplication.class, webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class ProductControllerIntegrationTest {

	@Autowired
	private TestRestTemplate restTemplate;


	private String getRootUrl() {
		return "http://localhost:8081/api/v1";
	}

	@Test
	public void contextLoads() {

	}

	@Test
	public void testGetProducts() {
		HttpHeaders headers = new HttpHeaders();
		UriComponentsBuilder builder = UriComponentsBuilder.fromHttpUrl(getRootUrl() + "/products")
				.queryParam("pageNumber", "0")
				.queryParam("size", "3");

		HttpEntity<String> entity = new HttpEntity<>(headers);
		ResponseEntity<String> response = restTemplate.exchange(builder.toUriString(),
				HttpMethod.GET, entity, String.class);
		assertNotNull(response.getBody());
		assertTrue(response.getBody().contains("\"numberOfElements\":3"));
		assertEquals(200, response.getStatusCodeValue());
	}

	@Test
	public void testGetProductsByCategoryId() {
		long categoryId = 788;
		HttpHeaders headers = new HttpHeaders();
		UriComponentsBuilder builder = UriComponentsBuilder.fromHttpUrl(getRootUrl() + "/products-by-category-id")
				.queryParam("categoryId", categoryId)
				.queryParam("pageNumber", "0")
				.queryParam("size", "3");

		HttpEntity<String> entity = new HttpEntity<>(headers);
		ResponseEntity<String> response = restTemplate.exchange(builder.toUriString(),
				HttpMethod.GET, entity, String.class);
		assertNotNull(response.getBody());
		assertTrue(response.getBody().contains("\"numberOfElements\":3"));
		assertEquals(200, response.getStatusCodeValue());
	}

	@Test
	public void testGetProductById() {
		Product product = new Product();
		product.setName("Product 1");
		HttpHeaders headers = new HttpHeaders();
		HttpEntity<Product> httpEntity = new HttpEntity<>(product, headers);
		ResponseEntity<Product> postResponse = restTemplate.postForEntity(getRootUrl() + "/products", httpEntity, Product.class);
		assertNotNull(postResponse);
		assertNotNull(postResponse.getBody());
		long id = postResponse.getBody().getId();
		Product newProd = restTemplate.getForObject(getRootUrl() + "/products/" + id, Product.class);
		assertEquals(id,newProd.getId());
		HttpEntity<?> deleteReq = new HttpEntity<>(headers);
		restTemplate.exchange(getRootUrl() + "/products/" + id, HttpMethod.DELETE, deleteReq, String.class);
	}

	@Test
	public void testDeleteProduct() {
		Product product = new Product();
		product.setName("Product 1");
		HttpHeaders headers = new HttpHeaders();
		HttpEntity<Product> httpEntity = new HttpEntity<>(product, headers);
		ResponseEntity<Product> postResponse = restTemplate.postForEntity(getRootUrl() + "/products", httpEntity, Product.class);
		assertNotNull(postResponse);
		assertNotNull(postResponse.getBody());
		long id = postResponse.getBody().getId();
		HttpEntity<?> deleteReq = new HttpEntity<>(headers);
		restTemplate.exchange(getRootUrl() + "/products/" + id, HttpMethod.DELETE, deleteReq, String.class);
		try {
			restTemplate.getForObject(getRootUrl() + "/products/" + id, Product.class);
		} catch (final HttpClientErrorException e) {
			assertEquals(e.getStatusCode(), HttpStatus.NOT_FOUND);
		}
	}

	@Test
	public void testCreateProduct() {
		Product product = new Product();
		product.setName("Product 1");
		product.setOnlineStatus(ACTIVE);
		product.setShortDescription("short description");
		product.setLongDescription("long description");
		HttpHeaders headers = new HttpHeaders();
		HttpEntity<Product> httpEntity = new HttpEntity<>(product, headers);
		ResponseEntity<Product> postResponse = restTemplate.postForEntity(getRootUrl() + "/products", httpEntity, Product.class);
		assertNotNull(postResponse);
		assertNotNull(postResponse.getBody());
		long id = postResponse.getBody().getId();
		Product newProduct = restTemplate.getForObject(getRootUrl() + "/products/" + id, Product.class);
		assertEquals(product.getName(), newProduct.getName());
		assertEquals(product.getOnlineStatus(), newProduct.getOnlineStatus());
		assertEquals(product.getShortDescription(), newProduct.getShortDescription());
		assertEquals(product.getLongDescription(), newProduct.getLongDescription());
		HttpEntity<?> deleteReq = new HttpEntity<>(headers);
		restTemplate.exchange(getRootUrl() + "/products/" + id, HttpMethod.DELETE, deleteReq, String.class);
	}

	@Test
	public void testCreateProductWithCategories() {
		/////////create categories
		HttpHeaders headers = new HttpHeaders();
		Category category1 = new Category();
		category1.setName("Category1");
		HttpEntity<Category> httpEntityCat = new HttpEntity<>(category1, headers);
		ResponseEntity<Category> postResponseCat = restTemplate.postForEntity(getRootUrl() + "/categories", httpEntityCat, Category.class);
		assertNotNull(postResponseCat.getBody());
		long catId1 = postResponseCat.getBody().getId();
		category1.setId(catId1);

		Category category2 = new Category();
		category2.setName("Category1");
		headers = new HttpHeaders();
		httpEntityCat = new HttpEntity<>(category2, headers);
		postResponseCat = restTemplate.postForEntity(getRootUrl() + "/categories", httpEntityCat, Category.class);
		assertNotNull(postResponseCat.getBody());
		long catId2 = postResponseCat.getBody().getId();
		category2.setId(catId2);

		Set<Category> categorySet = new HashSet<>();
		categorySet.add(category1);
		categorySet.add(category2);
		///////create product
		Product product = new Product();
		product.setName("Product 1");
		product.setOnlineStatus(ACTIVE);
		product.setShortDescription("short description");
		product.setLongDescription("long description");
		product.setCategories(categorySet);
		HttpEntity<Product> httpEntity = new HttpEntity<>(product, headers);
		ResponseEntity<Product> postResponse = restTemplate.postForEntity(getRootUrl() + "/products", httpEntity, Product.class);
		assertNotNull(postResponse.getBody());
		long prodId = postResponse.getBody().getId();

		Product newProduct = restTemplate.getForObject(getRootUrl() + "/products/" + prodId, Product.class);
		assertEquals(product.getName(), newProduct.getName());
		assertEquals(product.getOnlineStatus(), newProduct.getOnlineStatus());
		assertEquals(product.getShortDescription(), newProduct.getShortDescription());
		assertEquals(product.getLongDescription(), newProduct.getLongDescription());
		assertEquals(2,newProduct.getCategories().size());

		HttpEntity<?> deleteReq = new HttpEntity<>(headers);
		restTemplate.exchange(getRootUrl() + "/products/" + prodId, HttpMethod.DELETE, deleteReq, String.class);
		restTemplate.exchange(getRootUrl() + "/categories/" + catId1, HttpMethod.DELETE, deleteReq, String.class);
		restTemplate.exchange(getRootUrl() + "/categories/" + catId2, HttpMethod.DELETE, deleteReq, String.class);
	}

	@Test
	public void testCreateProductWithNotExistedCategories() {
		/////////create categories
		HttpHeaders headers = new HttpHeaders();
		Category category1 = new Category();
		category1.setName("Category1");
		HttpEntity<Category> httpEntityCat = new HttpEntity<>(category1, headers);
		ResponseEntity<Category> postResponseCat = restTemplate.postForEntity(getRootUrl() + "/categories", httpEntityCat, Category.class);
		assertNotNull(postResponseCat.getBody());
		long catId1 = postResponseCat.getBody().getId();
		category1.setId(catId1);

		Category category2 = new Category();
		category2.setName("Category2");
		category2.setId(99999);

		Set<Category> categorySet = new HashSet<>();
		categorySet.add(category1);
		categorySet.add(category2);
		///////create product
		Product product = new Product();
		product.setName("Product 1");
		product.setOnlineStatus(ACTIVE);
		product.setShortDescription("short description");
		product.setLongDescription("long description");
		product.setCategories(categorySet);
		HttpEntity<Product> httpEntity = new HttpEntity<>(product, headers);
		ResponseEntity<Product> postResponse = restTemplate.postForEntity(getRootUrl() + "/products", httpEntity, Product.class);
		assertEquals(404, postResponse.getStatusCodeValue());
		HttpEntity<?> deleteReq = new HttpEntity<>(headers);
		restTemplate.exchange(getRootUrl() + "/categories/" + catId1, HttpMethod.DELETE, deleteReq, String.class);
	}

	@Test
	public void testUpdateProduct() {
		Product product = new Product();
		product.setName("Product 1");
		product.setOnlineStatus(ACTIVE);
		product.setShortDescription("short description");
		product.setLongDescription("long description");
		HttpHeaders headers = new HttpHeaders();
		HttpEntity<Product> httpEntity = new HttpEntity<>(product, headers);
		ResponseEntity<Product> postResponse = restTemplate.postForEntity(getRootUrl() + "/products", httpEntity, Product.class);
		assertNotNull(postResponse);
		assertNotNull(postResponse.getBody());
		long id = postResponse.getBody().getId();

		Product productForUpdate = restTemplate.getForObject(getRootUrl() + "/products/" + id, Product.class);
		Product.OnlineStatus newOnlineStatus = productForUpdate.getOnlineStatus() == ACTIVE ? BLOCKED : ACTIVE;
		productForUpdate.setOnlineStatus(newOnlineStatus);
		productForUpdate.setName(product.getName()+" 1 ");
		productForUpdate.setShortDescription(product.getShortDescription()+" 1 ");
		productForUpdate.setLongDescription(product.getLongDescription()+" 1 ");
		headers = new HttpHeaders();
		HttpEntity<?> updateReq = new HttpEntity<>(productForUpdate, headers);
		restTemplate.exchange(getRootUrl() + "/products/" + id, HttpMethod.PUT, updateReq, Product.class);

		Product updatedProduct = restTemplate.getForObject(getRootUrl() + "/products/" + id, Product.class);
		assertNotNull(updatedProduct);
		assertEquals(productForUpdate.getName(), updatedProduct.getName());
		assertEquals(productForUpdate.getOnlineStatus(), updatedProduct.getOnlineStatus());
		assertEquals(productForUpdate.getShortDescription(), updatedProduct.getShortDescription());
		assertEquals(productForUpdate.getLongDescription(), updatedProduct.getLongDescription());
		HttpEntity<?> deleteReq = new HttpEntity<>(headers);
		restTemplate.exchange(getRootUrl() + "/products/" + id, HttpMethod.DELETE, deleteReq, String.class);
	}


}
