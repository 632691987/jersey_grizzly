package poc.jersey.demo.server.rest;

import org.springframework.beans.factory.annotation.Autowired;
import poc.jersey.demo.server.constraints.SerialNumber;
import poc.jersey.demo.server.exceptions.exception.BusinessException;
import poc.jersey.demo.server.model.Fruit;
import poc.jersey.demo.business.service.SimpleStorageService;
import org.glassfish.jersey.server.mvc.ErrorTemplate;
import org.glassfish.jersey.server.mvc.Template;
import org.glassfish.jersey.server.mvc.Viewable;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import javax.ws.rs.Consumes;
import javax.ws.rs.FormParam;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Path("/fruit")
public class FruitResource {

    @Autowired
    private SimpleStorageService simpleStorageService;

    /**
     * Triggered: PrematchingRequestFilter
     */
    @GET
    public Viewable get() {
        return new Viewable("/index.ftl", "Fruit Index Page");
    }

    /**
     * Triggered: PrematchingRequestFilter
     */
    @GET
    @Template(name = "/all.ftl")
    @Path("/all")
    @Produces(MediaType.TEXT_HTML)
    public Map<String, Object> getAllFruit() {
        final List<Fruit> fruits = new ArrayList<>();
        fruits.add(new Fruit("banana", "yellow"));
        fruits.add(new Fruit("apple", "red"));
        fruits.add(new Fruit("kiwi", "green"));

        final Map<String, Object> model = new HashMap<String, Object>();
        model.put("items", fruits);
        return model;
    }

    /**
     * Triggered: PrematchingRequestFilter
     */
    @GET
    @ErrorTemplate(name = "/error.ftl")
    @Template(name = "/named.ftl")
    @Path("{name}")
    @Produces(MediaType.TEXT_HTML)
    public String getFruitByName(@PathParam("name") String name) {
        if (!"banana".equalsIgnoreCase(name)) {
            throw new IllegalArgumentException("Fruit not found: " + name);
        }
        return name;
    }

    /**
     * Triggered:
     *      PrematchingRequestFilter
     *      RequestServerReaderInterceptor
     */
    @POST
    @Path("/create")
    @Consumes(MediaType.APPLICATION_FORM_URLENCODED)
    public void createFruit(
        @NotNull(message = "Fruit name must not be null") @FormParam("name") String name, 
        @NotNull(message = "Fruit colour must not be null") @FormParam("colour") String colour) {

        Fruit fruit = new Fruit(name, colour);
        simpleStorageService.storeFruit(fruit);
    }

    /**
     * Triggered:
     *      PrematchingRequestFilter
     *      RequestServerReaderInterceptor
     */
    @PUT
    @Path("/update")
    @Consumes(MediaType.APPLICATION_FORM_URLENCODED)
    public void updateFruit(@SerialNumber @FormParam("serial") String serial) {
        Fruit fruit = new Fruit();
        fruit.setSerial(serial);
        simpleStorageService.storeFruit(fruit);
    }

    /**
     * Triggered:
     *      PrematchingRequestFilter
     *      RequestServerReaderInterceptor
     */
    @POST
    @Path("/create")
    @Consumes(MediaType.APPLICATION_JSON)
    public void createFruit(@Valid Fruit fruit) {
        simpleStorageService.storeFruit(fruit);
    }
    
    @POST
    @Path("/created")
    @Consumes(MediaType.APPLICATION_JSON)
    public Response createNewFruit(@Valid Fruit fruit) {
        String result = "Fruit saved : " + fruit;
        return Response.status(Status.CREATED.getStatusCode()).entity(result).build();
    }

    @GET
    @Valid
    @Produces(MediaType.APPLICATION_JSON)
    @Path("/search/{name}")
    public Fruit findFruitByName(@PathParam("name") String name) {
        return simpleStorageService.findByName(name);
    }

    @GET
    @Produces(MediaType.TEXT_HTML)
    @Path("/exception")
    @Valid
    public Fruit exception() {
        throw new BusinessException();
    }
}
