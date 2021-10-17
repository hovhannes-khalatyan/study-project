package com.hovo.shop;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.HttpClientErrorException;

import com.hovo.shop.model.Category;
import org.springframework.web.util.UriComponentsBuilder;

import static org.junit.jupiter.api.Assertions.*;


@SpringBootTest(classes = ShopApplication.class, webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class CategoryControllerIntegrationTest {

	@Autowired
	private TestRestTemplate restTemplate;

	private String getRootUrl() {
		return "http://localhost:8081/api/v1";
	}

	@Test
	public void contextLoads() {

	}

	@Test
	public void testGetCategories() {
		HttpHeaders headers = new HttpHeaders();
		UriComponentsBuilder builder = UriComponentsBuilder.fromHttpUrl(getRootUrl() + "/categories")
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
	public void testGetCategoryById() {
		Category category = new Category();
		category.setName("Category1");
		HttpHeaders headers = new HttpHeaders();
		HttpEntity<Category> httpEntity = new HttpEntity<>(category, headers);
		ResponseEntity<Category> postResponse = restTemplate.postForEntity(getRootUrl() + "/categories", httpEntity, Category.class);
		assertNotNull(postResponse);
		assertNotNull(postResponse.getBody());
		long id = postResponse.getBody().getId();
		Category newCategory = restTemplate.getForObject(getRootUrl() + "/categories/" + id, Category.class);
		assertEquals(id, newCategory.getId());
		HttpEntity<?> deleteReq = new HttpEntity<>(headers);
		restTemplate.exchange(getRootUrl() + "/categories/" + id, HttpMethod.DELETE, deleteReq, String.class);
	}

	@Test
	public void testDeleteCategory() {
		Category category = new Category();
		category.setName("Category1");
		HttpHeaders headers = new HttpHeaders();
		HttpEntity<Category> httpEntity = new HttpEntity<>(category, headers);
		ResponseEntity<Category> postResponse = restTemplate.postForEntity(getRootUrl() + "/categories", httpEntity, Category.class);
		assertNotNull(postResponse);
		assertNotNull(postResponse.getBody());
		long id = postResponse.getBody().getId();
		HttpEntity<?> deleteReq = new HttpEntity<>(headers);
		restTemplate.exchange(getRootUrl() + "/categories/" + id, HttpMethod.DELETE, deleteReq, String.class);
		try {
			restTemplate.getForObject(getRootUrl() + "/categories/" + id, Category.class);
		} catch (final HttpClientErrorException e) {
			assertEquals(e.getStatusCode(), HttpStatus.NOT_FOUND);
		}
	}


	@Test
	public void testDeleteAttachedCategory() {
		long id = 788;
		HttpHeaders headers = new HttpHeaders();
		HttpEntity<?> deleteReq = new HttpEntity<>(headers);
		ResponseEntity<String> res = restTemplate.exchange(getRootUrl() + "/categories/" + id, HttpMethod.DELETE, deleteReq, String.class);
		assertEquals(403, res.getStatusCodeValue());
		Category category = restTemplate.getForObject(getRootUrl() + "/categories/" + id, Category.class);
		assertEquals(id, category.getId());
	}

	@Test
	public void testCreateCategoryWithParentCategory() {
		Category parentCategory = new Category();
		parentCategory.setName("parent category");
		HttpHeaders headers = new HttpHeaders();
		HttpEntity<Category> httpEntity = new HttpEntity<>(parentCategory, headers);
		ResponseEntity<Category> postResponse = restTemplate.postForEntity(getRootUrl() + "/categories", httpEntity, Category.class);
		assertNotNull(postResponse);
		assertNotNull(postResponse.getBody());
		long parentId = postResponse.getBody().getId();
		parentCategory.setId(parentId);

		Category category = new Category();
		category.setName("Category1");
		category.setParentCategory(parentCategory);
		headers = new HttpHeaders();
		httpEntity = new HttpEntity<>(category, headers);
		postResponse = restTemplate.postForEntity(getRootUrl() + "/categories", httpEntity, Category.class);
		assertNotNull(postResponse);
		assertNotNull(postResponse.getBody());
		long id = postResponse.getBody().getId();
		Category createdCategory = restTemplate.getForObject(getRootUrl() + "/categories/" + id, Category.class);
		assertNotNull(createdCategory);
		assertEquals(createdCategory.getName(), category.getName());
		assertEquals(createdCategory.getParentCategory().getId(), parentCategory.getId());
		assertEquals(createdCategory.getParentCategory().getName(), parentCategory.getName());
		HttpEntity<?> deleteReq = new HttpEntity<>(headers);
		restTemplate.exchange(getRootUrl() + "/categories/" + id, HttpMethod.DELETE, deleteReq, String.class);
		restTemplate.exchange(getRootUrl() + "/categories/" + parentId, HttpMethod.DELETE, deleteReq, String.class);

	}

	@Test
	public void testUpdateCategory() {
		//create category
		Category newCategory = new Category();
		newCategory.setName("Category1");
		HttpHeaders headers = new HttpHeaders();
		HttpEntity<Category> httpEntity = new HttpEntity<>(newCategory, headers);
		ResponseEntity<Category> postResponse = restTemplate.postForEntity(getRootUrl() + "/categories", httpEntity, Category.class);
		assertNotNull(postResponse);
		assertNotNull(postResponse.getBody());
		long id = postResponse.getBody().getId();

		//update category
		Category category = restTemplate.getForObject(getRootUrl() + "/categories/" + id, Category.class);
		category.setName(category.getName()+" 1 ");

		headers = new HttpHeaders();
		HttpEntity<?> updateReq = new HttpEntity<>(category, headers);
		restTemplate.exchange(getRootUrl() + "/categories/" + id, HttpMethod.PUT, updateReq, Category.class);

		Category updatedCategory = restTemplate.getForObject(getRootUrl() + "/categories/" + id, Category.class);
		assertNotNull(updatedCategory);
		assertEquals(updatedCategory.getName(), category.getName());

		HttpEntity<?> deleteReq = new HttpEntity<>(headers);
		restTemplate.exchange(getRootUrl() + "/categories/" + id, HttpMethod.DELETE, deleteReq, String.class);


	}
}
