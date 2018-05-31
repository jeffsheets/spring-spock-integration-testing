package com.objectpartners.eskens.controllers

import com.objectpartners.eskens.config.IntegrationTestMockingConfig
import com.objectpartners.eskens.model.Rank
import com.objectpartners.eskens.services.ExternalRankingService
import com.objectpartners.eskens.services.ValidatedExternalRankingService
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.context.annotation.Import
import org.springframework.test.util.AopTestUtils
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.MvcResult
import spock.lang.Specification

import static org.springframework.http.MediaType.APPLICATION_JSON
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status

/**
 * An integration test illustrating how to wire everything w/ Spring,
 * but replace certain components with Spock mocks
 * Created by derek on 4/10/17.
 */
@SpringBootTest
@AutoConfigureMockMvc
@Import([IntegrationTestMockingConfig]) //See additional notes at the bottom
class PersonControllerIntTest extends Specification {

    @Autowired MockMvc mvc

    /**
     * This is our mock we created in our test config. We inject it in so we can control it in our specs.
     */
    @Autowired ExternalRankingService externalRankingServiceMock

    @Autowired
    ValidatedExternalRankingService proxiedValidatedExternalRankingService

    ValidatedExternalRankingService validatedExternalRankingService

    void setup() {
        /**
         * the Validated proxied ValidatedExternalRankingService must be unwrapped to use the Mock
         *
         *   see PersonControllerIntTest to see how to make this easier with @SpringSpy @UnwrapAopProxy
         */
        validatedExternalRankingService = AopTestUtils.getUltimateTargetObject(proxiedValidatedExternalRankingService)
    }

    def "GetRank"() {
        when: 'Calling getRank for a known seed data entity'
        MvcResult mvcResult = mvc.perform(get("/persons/1/rank").contentType(APPLICATION_JSON))
                                .andExpect(status().is2xxSuccessful()).andReturn()

        then: 'we define the mock for JUST the external service'
        externalRankingServiceMock.getRank(_) >> {
            new Rank(level: 1, classification: 'Captain')
        }
        noExceptionThrown()

        when: 'inspecting the contents'
        def resultingJson = mvcResult.response.contentAsString

        then: 'the result contains a mix of mocked service data and actual wired component data'
        resultingJson == 'Capt James Kirk ~ Captain:Level 1'
    }

    def "GetValidatedRank"() {
        when: 'Calling getRank for a known seed data entity'
        MvcResult mvcResult = mvc.perform(get("/persons/1/validatedRank").contentType(APPLICATION_JSON))
                .andExpect(status().is2xxSuccessful()).andReturn()

        then: 'we define the mock for the external service'
        1 * validatedExternalRankingService.getRank(_) >> {
            new Rank(level: 1, classification: 'Captain')
        }
        noExceptionThrown()

        when: 'inspecting the contents'
        def resultingJson = mvcResult.response.contentAsString

        then: 'the result contains a mix of mocked service data and actual wired component data'
        resultingJson == 'Capt James Kirk ~ Captain:Level 1'
    }


    /*
        We could define our test configuration here, but if we have multiple integration tests
        and we want to mock the same things, then it's better to share the configuration for context caching,
        thus the import of IntegrationTestMockingConfig


        If you are using Spock 1.2, see PersonControllerInSpock12Test for usage of @SpringBean annotation

        Otherwise use the below code for Spock <= 1.1
     */

    /*
    @TestConfiguration
    static class Config {
        private DetachedMockFactory factory = new DetachedMockFactory()

        @Bean
        ExternalRankingService externalRankingService() {
            factory.Mock(ExternalRankingService)
        }

        @Bean
        ValidatedExternalRankingService validatedExternalRankingService() {
            factory.Mock(ValidatedExternalRankingService)
        }
    }
    */
}
