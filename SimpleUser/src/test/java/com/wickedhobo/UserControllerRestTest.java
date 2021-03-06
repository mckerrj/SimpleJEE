package com.wickedhobo;

import static org.hamcrest.Matchers.containsInAnyOrder;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.hasProperty;
import static org.hamcrest.Matchers.hasSize;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.model;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.HashSet;
import java.util.Set;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.context.WebApplicationContext;

import com.wickedhobo.DAO.hibernateDAO.UserDAOHibSpringImpl;
import com.wickedhobo.object.Role;
import com.wickedhobo.object.User;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = { "file:src/main/webapp/WEB-INF/rest-servlet.xml", "classpath:applicationContext.xml" })
@WebAppConfiguration
public class UserControllerRestTest {

  @Autowired
  private WebApplicationContext ctx;
  @Autowired
  UserDAOHibSpringImpl userDAO;
  private MockMvc mockMvc;
  private static Logger log = LoggerFactory.getLogger(UserControllerTest.class);

  @Before
  public void setUp() {
    this.mockMvc = MockMvcBuilders.webAppContextSetup(ctx).build();
  }

  @Test
  @Transactional()
  // @Rollback(false) //Left here for debug purposes.
  public void testAddUserController() throws Exception {
    mockMvc.perform(post("/addUser/" +
        "userName/{userName}/" +
        "firstName/{firstName}/" +
        "lastName/{lastName}/" +
        "password/{password}",
        "halgrena",
        "Anne",
        "Halgren",
        "anne314")
        .accept(MediaType.APPLICATION_JSON))
        .andDo(print())
        .andExpect(status().isOk());

    User user2 = userDAO.findUserByUsername("halgrena");
    assertNotNull(user2);
    assertEquals("Halgren", user2.getLastName());
    assertEquals("Anne", user2.getFirstName());
    assertEquals("anne314", user2.getPassword());
    log.debug("UserControllerRestTest.testAddUserController has passed all tests!");
  }

  @Test
  @Transactional()
  public void testUpdateUserController() throws Exception {

    User user = new User();

    user.setFirstName("Anne");
    user.setLastName("Halgren");
    user.setUserName("halgrena");
    user.setPassword("anne314");
    Role role = new Role();
    role.setRole("administrator");
    Set<Role> roles = new HashSet<Role>();
    roles.add(role);
    user.setRoles(roles);

    userDAO.saveUser(user);

    mockMvc.perform(put("/updateUser/" +
        "userName/{userName}/" +
        "firstName/{firstName}/" +
        "lastName/{lastName}/" +
        "password/{password}",
        "halgrena",
        "Anne2",
        "Halgren2",
        "anne3142").
        accept(MediaType.APPLICATION_JSON)).
        andDo(print()).
        andExpect(status().isOk());

    User user2 = userDAO.findUserByUsername("halgrena");
    assertNotNull(user2);
    assertEquals("Halgren2", user2.getLastName());
    assertEquals("Anne2", user2.getFirstName());
    assertEquals("anne3142", user2.getPassword());
    log.debug("UserControllerRestTest.testUpdateUserController has passed all tests!");
  }

  @Test
  public void testRemoveUserController() throws Exception {

    User user = new User();

    user.setFirstName("Anne");
    user.setLastName("Halgren");
    user.setUserName("halgrena");
    user.setPassword("anne314");
    Role role = new Role();
    role.setRole("administrator");
    Set<Role> roles = new HashSet<Role>();
    roles.add(role);
    user.setRoles(roles);

    userDAO.saveUser(user);

    mockMvc.perform(delete("/removeUser/" +
        "userName/{userName}/",
        "halgrena").
        accept(MediaType.APPLICATION_JSON))
        .andDo(print())
        .andExpect(status().isOk());

    User user2 = userDAO.findUserByUsername("halgrena");
    assertNull(user2);
    log.debug("UserControllerRestTest.testRemoveUserController has passed all tests!");
  }

  @Test
  @Transactional()
  public void testFindUserByUsernameController() throws Exception {

    User user = new User();

    user.setFirstName("Anne");
    user.setLastName("Halgren");
    user.setUserName("halgrena");
    user.setPassword("anne314");
    Role role = new Role();
    Role role2 = new Role();
    role.setRole("administrator");
    role2.setRole("developer");
    Set<Role> roles = new HashSet<Role>();
    roles.add(role);
    roles.add(role2);
    user.setRoles(roles);

    userDAO.saveUser(user);

    mockMvc.perform(get("/findUserByUsername/userName/{userName}", user.getUserName())
        .accept(MediaType.APPLICATION_JSON))
        .andDo(print())
        .andExpect(status().isOk())
        .andExpect(content().contentType("application/json;charset=UTF-8"))
        .andExpect(jsonPath("userName", equalTo("halgrena")))
        .andExpect(jsonPath("firstName", equalTo("Anne")))
        .andExpect(jsonPath("lastName", equalTo("Halgren")))
        .andExpect(jsonPath("password", equalTo("anne314")))
        .andExpect(jsonPath("$.roles", hasSize(2)))
        .andExpect(jsonPath("$.roles..role", containsInAnyOrder("administrator", "developer")));
    log.debug("UserControllerRestTest.testFindUserByUsernameController has passed all tests!");
  }

  @Test
  @Transactional()
  public void testFindUserByUsernameWithActionController() throws Exception {

    User user = new User();

    user.setFirstName("Anne");
    user.setLastName("Halgren");
    user.setUserName("halgrena");
    user.setPassword("anne314");
    Role role = new Role();
    role.setRole("administrator");
    Set<Role> roles = new HashSet<Role>();
    roles.add(role);
    user.setRoles(roles);

    userDAO.saveUser(user);

    mockMvc.perform(get("/findUserByUsernameWithAction/userName/{userName}", user.getUserName())
        .accept(MediaType.APPLICATION_JSON))
        .andDo(print())
        .andExpect(status().isOk())
        .andExpect(content().contentType("application/json"))
        .andExpect(model().attribute("userAction", "findUserByUsername"))
        .andExpect(model().attribute("user", hasProperty("firstName", equalTo("Anne"))))
        .andExpect(model().attribute("user", hasProperty("lastName", equalTo("Halgren"))))
        .andExpect(model().attribute("user", hasProperty("password", equalTo("anne314"))))
        .andExpect(jsonPath("$.user.roles", hasSize(1)))
        .andExpect(jsonPath("$.user.roles[0].role", equalTo("administrator"))); //Doing this one different than in testFindByUsernameController just for giggles, show to different ways of traversing using Json Path
    log.debug("UserControllerRestTest.testFindUserByUsernameWithActionController has passed all tests!");
  }

  @Test
  @Transactional
  public void testListUsersController() throws Exception {
    mockMvc.perform(get("/listUsers").accept(MediaType.APPLICATION_JSON))
        .andDo(print())
        .andExpect(status().isOk())
        .andExpect(content().contentType("application/json;charset=UTF-8"))
        .andExpect(jsonPath("$.", hasSize(2)))
        .andExpect(jsonPath("$..userName", containsInAnyOrder("mckerrj", "gordond")))
        .andExpect(jsonPath("$..firstName", containsInAnyOrder("Jason", "Dexter")))
        .andExpect(jsonPath("$..lastName", containsInAnyOrder("Gordon", "McKerr")))
        .andExpect(jsonPath("$..password", containsInAnyOrder("gordond314", "mckerrj314")));
    log.debug("UserControllerRestTest.testListUsersController has passed all tests!");
  }

  @Test
  @Transactional
  public void testListUsersByRoleController() throws Exception {
    mockMvc.perform(get("/listUsersByRole/roleName/{roleName}", "administrator").accept(MediaType.APPLICATION_JSON))
        .andDo(print())
        .andExpect(status().isOk())
        .andExpect(content().contentType("application/json;charset=UTF-8"))
        .andExpect(jsonPath("$.", hasSize(2)))
        .andExpect(jsonPath("$..userName", containsInAnyOrder("mckerrj", "gordond")))
        .andExpect(jsonPath("$..firstName", containsInAnyOrder("Jason", "Dexter")))
        .andExpect(jsonPath("$..lastName", containsInAnyOrder("Gordon", "McKerr")))
        .andExpect(jsonPath("$..password", containsInAnyOrder("gordond314", "mckerrj314")));

    mockMvc.perform(get("/listUsersByRole/roleName/{roleName}", "developer").accept(MediaType.APPLICATION_JSON))
        .andDo(print())
        .andExpect(status().isOk())
        .andExpect(content().contentType("application/json;charset=UTF-8"))
        .andExpect(jsonPath("$.", hasSize(1)))
        .andExpect(jsonPath("$..userName", containsInAnyOrder("mckerrj")))
        .andExpect(jsonPath("$..firstName", containsInAnyOrder("Jason")))
        .andExpect(jsonPath("$..lastName", containsInAnyOrder("McKerr")))
        .andExpect(jsonPath("$..password", containsInAnyOrder("mckerrj314")));

    mockMvc.perform(get("/listUsersByRole/roleName/{roleName}", "saxplayer").accept(MediaType.APPLICATION_JSON))
        .andDo(print())
        .andExpect(status().isOk())
        .andExpect(content().contentType("application/json;charset=UTF-8"))
        .andExpect(jsonPath("$.", hasSize(1)))
        .andExpect(jsonPath("$..userName", containsInAnyOrder("gordond")))
        .andExpect(jsonPath("$..firstName", containsInAnyOrder("Dexter")))
        .andExpect(jsonPath("$..lastName", containsInAnyOrder("Gordon")))
        .andExpect(jsonPath("$..password", containsInAnyOrder("gordond314")));

    log.debug("UserControllerRestTest.testListUsersByRoleController has passed all tests!");
  }
}
