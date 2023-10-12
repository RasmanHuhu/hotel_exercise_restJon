package dk.lyngby.dao.impl;

import dk.lyngby.config.ApplicationConfig;
import dk.lyngby.config.HibernateConfig;
import dk.lyngby.controller.impl.HotelController;
import dk.lyngby.model.Hotel;
import dk.lyngby.model.Room;
import io.javalin.Javalin;
import jakarta.persistence.EntityManagerFactory;
import org.jetbrains.annotations.NotNull;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

class HotelDaoTest {

    //private static Javalin app;
    //private static final String BASE_URL = "http://localhost:7777/api/v1";
    private static HotelDao hotelDao;
    private static EntityManagerFactory emfTest;

    private static Hotel h1, h2;

    @BeforeAll
    static void beforeAll()
    {
        HibernateConfig.setTest(true);
        emfTest = HibernateConfig.getEntityManagerFactory();
        hotelDao = HotelDao.getInstance(emfTest);
    }

    @BeforeEach
    void setUp()
    {
        Set<Room> calRooms = getCalRooms();
        Set<Room> hilRooms = getBatesRooms();

        try (var em = emfTest.createEntityManager())
        {
            em.getTransaction().begin();
            // Delete all rows
            em.createQuery("DELETE FROM Room r").executeUpdate();
            em.createQuery("DELETE FROM Hotel h").executeUpdate();
            // Reset sequence
            em.createNativeQuery("ALTER SEQUENCE room_room_id_seq RESTART WITH 1").executeUpdate();
            em.createNativeQuery("ALTER SEQUENCE hotel_hotel_id_seq RESTART WITH 1").executeUpdate();
            // Insert test data
            h1 = new Hotel("Hotel California", "California", Hotel.HotelType.LUXURY);
            h2 = new Hotel("Bates Motel", "Lyngby", Hotel.HotelType.STANDARD);
            h1.setRooms(calRooms);
            h2.setRooms(hilRooms);
            em.persist(h1);
            em.persist(h2);
            em.getTransaction().commit();

            //app = Javalin.create();
            //ApplicationConfig.startServer(app, 7777);
        }
    }

    @AfterEach
    void tearDown()
    {
        HibernateConfig.setTest(false);
        //ApplicationConfig.stopServer(app);
    }


    @Test
    void read() {
        Hotel hotelActually = hotelDao.read(h1.getId());
        assertEquals(h1, hotelActually);
    }

    @Test
    void readAll() {
        List<Hotel> hotelListExpected = new ArrayList<>(List.of(h1,h2));

        List<Hotel> hotelListActually = hotelDao.readAll();

        assertEquals(hotelListExpected, hotelListActually);
    }

    @Test
    void create() {
        // Hotel(String hotelName, String hotelAddress, HotelType hotelType)
        Hotel expected = new Hotel("The Expected Hotel",
                "The Expected street 2",
                Hotel.HotelType.LUXURY);

        Set<Room> roomSet = getCalRooms();
        expected.setRooms(roomSet);

        expected = hotelDao.create(expected);

        Hotel actual = hotelDao.read(expected.getId());

        assertEquals(expected, actual);
    }

    @Test
    void update() {
        Hotel expected = hotelDao.read(h1.getId());
        expected.setHotelName("The Expected Hotel");
        expected = hotelDao.update(h1.getId(), expected);

        Hotel actual = hotelDao.read(h1.getId());

        assertEquals(expected, actual);

    }

    @Test
    void delete() {
        int id = h1.getId();
        hotelDao.delete(h1);

        Hotel h = hotelDao.read(id);

        assertNull(h);
    }



    @NotNull
    private static Set<Room> getCalRooms()
    {
        Room r100 = new Room(100, new BigDecimal(2520), Room.RoomType.SINGLE);
        Room r101 = new Room(101, new BigDecimal(2520), Room.RoomType.SINGLE);
        Room r102 = new Room(102, new BigDecimal(2520), Room.RoomType.SINGLE);
        Room r103 = new Room(103, new BigDecimal(2520), Room.RoomType.SINGLE);
        Room r104 = new Room(104, new BigDecimal(3200), Room.RoomType.DOUBLE);
        Room r105 = new Room(105, new BigDecimal(4500), Room.RoomType.SUITE);

        Room[] roomArray = {r100, r101, r102, r103, r104, r105};
        return Set.of(roomArray);
    }

    @NotNull
    private static Set<Room> getBatesRooms()
    {
        Room r111 = new Room(111, new BigDecimal(2520), Room.RoomType.SINGLE);
        Room r112 = new Room(112, new BigDecimal(2520), Room.RoomType.SINGLE);
        Room r113 = new Room(113, new BigDecimal(2520), Room.RoomType.SINGLE);
        Room r114 = new Room(114, new BigDecimal(2520), Room.RoomType.DOUBLE);
        Room r115 = new Room(115, new BigDecimal(3200), Room.RoomType.DOUBLE);
        Room r116 = new Room(116, new BigDecimal(4500), Room.RoomType.SUITE);

        Room[] roomArray = {r111, r112, r113, r114, r115, r116};
        return Set.of(roomArray);
    }
}