package com.sliit.vehiclerentalsystem01.core.config;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.util.FileSystemUtils;

import java.io.File;
import java.io.FileOutputStream;
import java.nio.charset.StandardCharsets;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest(properties = "app.data.directory=target/test-data")
@AutoConfigureMockMvc
public class WebMvcConfigTest {

    @Autowired
    private MockMvc mockMvc;

    private File testDataDir;

    @BeforeEach
    public void setUp() {
        testDataDir = new File("target/test-data");
        if (!testDataDir.exists()) {
            testDataDir.mkdirs();
        }
    }

    @AfterEach
    public void tearDown() {
        if (testDataDir.exists()) {
            FileSystemUtils.deleteRecursively(testDataDir);
        }
    }

    @Test
    public void testVehicleImageIsServed() throws Exception {
        File vehiclesDir = new File(testDataDir, "vehicles");
        if (!vehiclesDir.exists()) {
            vehiclesDir.mkdirs();
        }
        File testFile = new File(vehiclesDir, "test-image.jpg");
        try (FileOutputStream fos = new FileOutputStream(testFile)) {
            fos.write("dummy-image-content".getBytes(StandardCharsets.UTF_8));
        }

        mockMvc.perform(get("/images/vehicles/test-image.jpg"))
                .andExpect(status().isOk());
    }

    @Test
    public void testVehicleImageUpload() throws Exception {
        MockMultipartFile imageFile = new MockMultipartFile(
                "imageFile",
                "my-car.png",
                "image/png",
                "png-bytes-here".getBytes(StandardCharsets.UTF_8)
        );

        mockMvc.perform(multipart("/vehicles")
                        .file(imageFile)
                        .param("vehicleType", "Car")
                        .param("brand", "Tesla")
                        .param("model", "Model S")
                        .param("rate", "120.00")
                        .param("capacity", "5")
                        .param("status", "AVAILABLE")
                        .sessionAttr("adminLoggedIn", true))
                .andExpect(status().is3xxRedirection());

        File vehiclesDir = new File(testDataDir, "vehicles");
        assertTrue(vehiclesDir.exists());
        File[] files = vehiclesDir.listFiles();
        assertTrue(files != null && files.length > 0);

        boolean foundPng = false;
        for (File f : files) {
            if (f.getName().endsWith(".png")) {
                foundPng = true;
                break;
            }
        }
        assertTrue(foundPng, "Uploaded PNG image was not saved correctly to the vehicles directory");
    }
}
