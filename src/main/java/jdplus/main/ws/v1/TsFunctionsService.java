package jdplus.main.ws.v1;

import static jakarta.ws.rs.core.MediaType.APPLICATION_JSON;

import io.quarkus.grpc.GrpcService;
import io.quarkus.runtime.annotations.RegisterForReflection;
import io.smallrye.mutiny.Multi;
import io.smallrye.mutiny.Uni;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import org.eclipse.microprofile.openapi.annotations.media.Content;
import org.eclipse.microprofile.openapi.annotations.media.ExampleObject;
import org.eclipse.microprofile.openapi.annotations.parameters.RequestBody;

@Path("/hello")
@Consumes(APPLICATION_JSON)
@Produces(APPLICATION_JSON)
@GrpcService
@RegisterForReflection
public class TsFunctionsService implements TsFunctions {

    @Override
    public Uni<VersionInfoDto> getVersion(EmptyDto request) {
        // TODO: improve version management
        return Uni.createFrom()
                .item(VersionInfoDto.newBuilder()
                        .setMajor(0)
                        .setMinor(2)
                        .setRevision(0)
                        .build());
    }

    @RequestBody(content = @Content(examples = @ExampleObject(name = "Example 1", value = """
                                    {
                                      "id": "Example 1",
                                      "series": {
                                        "start": {
                                          "frequency": "FREQ_YEARLY",
                                          "year": 2010,
                                          "pos": 0
                                        },
                                        "values": [ 3.0, 4.0 ]
                                      }
                                    }
                                    """)))
    @POST
    @Path("/normalize")
    @Override
    public Uni<TsFunctionResponseDto> normalize(TsFunctionRequestDto request) {
        return Uni.createFrom().item(Helpers.normalize(request));
    }

    @RequestBody(content = @Content(examples = @ExampleObject(name = "Example 1", value = """
                                    {
                                      "id": "Example 1",
                                      "series": {
                                        "start": {
                                          "frequency": "FREQ_YEARLY",
                                          "year": 2010,
                                          "pos": 0
                                        },
                                        "values": [ 0, 1, 2, 3, 4, 5, 6, 7 ]
                                      }
                                    }
                                    """)))
    @POST
    @Path("/statistics")
    @Override
    public Uni<DescriptiveStatisticsResponseDto> statistics(TsFunctionRequestDto request) {
        return Uni.createFrom().item(Helpers.statistics(request));
    }

    @POST
    @Path("/pct")
    @Override
    public Uni<TsFunctionResponseDto> pct(PctRequestDto request) {
        return Uni.createFrom().item(Helpers.pct(request));
    }

    @POST
    @Path("/delta")
    @Override
    public Uni<TsFunctionResponseDto> delta(DeltaRequestDto request) {
        return Uni.createFrom().item(Helpers.delta(request));
    }

    @POST
    @Path("/aggregate")
    @Override
    public Uni<TsFunctionResponseDto> aggregate(AggregationRequestDto request) {
        return Uni.createFrom().item(Helpers.aggregate(request));
    }

    @POST
    @Path("/hodrickPrescott")
    @Override
    public Uni<HodrickPrescottResponseDto> hodrickPrescott(HodrickPrescottRequestDto request) {
        return Uni.createFrom().item(Helpers.hodrickPrescott(request));
    }

    @RequestBody(content = @Content(examples = @ExampleObject(name = "Example 1", value = """
                                    {
                                      "id": "Example 1",
                                      "gathering": {
                                        "frequency": "FREQ_UNDEFINED",
                                        "aggregationType": "AGGREGATION_NONE",
                                        "allowPartialAggregation": false,
                                        "includeMissingValues": false
                                      },
                                      "observations": [
                                        {
                                          "date": { "year": 2010, "month": 1, "day": 1 },
                                          "value": 11
                                        },
                                        {
                                          "date": { "year": 2010, "month": 2, "day": 1 },
                                          "value": 22
                                        }
                                      ]
                                    }
                                    """)))
    @POST
    @Path("/buildTsData")
    @Override
    public Uni<BuildTsDataResponseDto> buildTsData(BuildTsDataRequestDto request) {
        return Uni.createFrom().item(Helpers.buildTsData(request));
    }

    @Override
    public Multi<TsFunctionResponseDto> normalizeStream(Multi<TsFunctionRequestDto> request) {
        return request.onItem().transform(Helpers::normalize);
    }

    @Override
    public Multi<DescriptiveStatisticsResponseDto> statisticsStream(Multi<TsFunctionRequestDto> request) {
        return request.onItem().transform(Helpers::statistics);
    }

    @Override
    public Multi<TsFunctionResponseDto> pctStream(Multi<PctRequestDto> request) {
        return request.onItem().transform(Helpers::pct);
    }

    @Override
    public Multi<TsFunctionResponseDto> deltaStream(Multi<DeltaRequestDto> request) {
        return request.onItem().transform(Helpers::delta);
    }

    @Override
    public Multi<TsFunctionResponseDto> aggregateStream(Multi<AggregationRequestDto> request) {
        return request.onItem().transform(Helpers::aggregate);
    }

    @Override
    public Multi<HodrickPrescottResponseDto> hodrickPrescottStream(Multi<HodrickPrescottRequestDto> request) {
        return request.onItem().transform(Helpers::hodrickPrescott);
    }

    @Override
    public Multi<BuildTsDataResponseDto> buildTsDataStream(Multi<BuildTsDataRequestDto> request) {
        return request.onItem().transform(Helpers::buildTsData);
    }

    @RequestBody(content = @Content(examples = @ExampleObject(name = "Example 1", value = """
                                    {
                                      "id": "Example 1",
                                      "distributionType": "DIST_FIRST",
                                      "collection": [
                                        {
                                          "start": {
                                            "frequency": "FREQ_QUARTERLY",
                                            "year": 2010
                                          },
                                          "values": [
                                            1.1
                                          ]
                                        },
                                        {
                                          "start": {
                                            "frequency": "FREQ_MONTHLY",
                                            "year": 2010
                                          },
                                          "values": [
                                            2.1,
                                            2.2
                                          ]
                                        }
                                      ]
                                    }
                                    """)))
    @POST
    @Path("/buildTsDataTable")
    @Override
    public Uni<BuildTsDataTableResponseDto> buildTsDataTable(BuildTsDataTableRequestDto request) {
        return Uni.createFrom().item(Helpers.buildTsDataTable(request));
    }

    @Override
    public Uni<TemporalDisaggregationResponseDto> processTemporalDisaggregation(
            TemporalDisaggregationRequestDto request) {
        return Uni.createFrom().item(Helpers.processTemporalDisaggregation(request));
    }

    @Override
    public Uni<TramoForecastResponseDto> tramoForecast(TramoRequestDto request) {
        return Uni.createFrom().item(Helpers.tramoForecast(request));
    }

    @Override
    public Multi<TramoForecastResponseDto> tramoForecastStream(Multi<TramoRequestDto> request) {
        return request.onItem().transform(Helpers::tramoForecast);
    }

    @Override
    public Uni<TramoFullProcessResponseDto> tramoFullProcess(TramoRequestDto request) {
        return Uni.createFrom().item(Helpers.tramoFullProcess(request));
    }
}
