package org.airsonic.player.service.metadata;

import org.airsonic.player.TestCaseUtils;
import org.airsonic.player.service.SettingsService;
import org.airsonic.player.util.HomeRule;
import org.apache.commons.io.FileUtils;
import org.junit.*;
import org.junit.rules.TemporaryFolder;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.junit4.SpringRunner;

import java.io.File;
import java.io.IOException;

import static org.hamcrest.CoreMatchers.instanceOf;
import static org.junit.Assert.assertThat;

@RunWith(SpringRunner.class)
@SpringBootTest
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
public class MetaDataFactoryTestCase {

    @ClassRule
    public static final HomeRule airsonicRule = new HomeRule();

    @ClassRule
    public static TemporaryFolder temporaryFolder = new TemporaryFolder();

    private static File someMp3;
    private static File someFlv;
    private static File someJunk;

    @BeforeClass
    public static void createTestFiles() throws IOException {
        String homeParent = TestCaseUtils.airsonicHomePathForTest();
        
        File dbDirectory = new File(homeParent, "/db");
        FileUtils.forceMkdir(dbDirectory);
        org.airsonic.player.util.FileUtils.copyResourcesRecursively(MetaDataFactoryTestCase.class.getResource("/db/pre-liquibase/db"), new File(homeParent));
        
        someMp3 = temporaryFolder.newFile("some.mp3");
        someFlv = temporaryFolder.newFile("some.flv");
        someJunk = temporaryFolder.newFile("some.junk");
    }

    @Autowired
    MetaDataParserFactory metaDataParserFactory;

    @Autowired
    SettingsService settingsService;

    @Test
    public void testorder() {
        MetaDataParser parser;

        settingsService.setVideoFileTypes("mp3 flv");

        parser = metaDataParserFactory.getParser(someMp3);
        assertThat(parser, instanceOf(JaudiotaggerParser.class));

        parser = metaDataParserFactory.getParser(someFlv);
        assertThat(parser, instanceOf(FFmpegParser.class));

        parser = metaDataParserFactory.getParser(someJunk);
        assertThat(parser, instanceOf(DefaultMetaDataParser.class));
    }

}
