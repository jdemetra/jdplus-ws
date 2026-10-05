package jdplus.main.ws.v1;

import io.quarkus.grpc.GrpcService;
import io.quarkus.runtime.annotations.RegisterForReflection;
import io.smallrye.mutiny.Multi;
import io.smallrye.mutiny.Uni;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jdplus.benchmarking.base.api.univariate.TemporalDisaggregation;
import org.eclipse.microprofile.openapi.annotations.media.Content;
import org.eclipse.microprofile.openapi.annotations.media.ExampleObject;
import org.eclipse.microprofile.openapi.annotations.parameters.RequestBody;


import static jakarta.ws.rs.core.MediaType.APPLICATION_JSON;

@Path("/hello")
@Consumes(APPLICATION_JSON)
@Produces(APPLICATION_JSON)
@GrpcService
@RegisterForReflection
public class TsFunctionsService implements TsFunctions {

    @Override
    public Uni<VersionInfoDto> getVersion(EmptyDto request) {
        // TODO: improve version management
        return Uni.createFrom().item(VersionInfoDto.newBuilder()
                .setMajor(0)
                .setMinor(0)
                .setRevision(1)
                .build());
    }

    @RequestBody(
            content = @Content(
                    examples = @ExampleObject(
                            name = "Example 1",
                            value = """
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
                                    """
                    )
            )
    )
    @POST
    @Path("/normalize")
    @Override
    public Uni<TsFunctionOutputDto> normalize(TsFunctionInputDto request) {
        return Uni.createFrom().item(Helpers.normalize(request));
    }

    @RequestBody(
            content = @Content(
                    examples = @ExampleObject(
                            name = "Example 1",
                            value = """
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
                                    """
                    )
            )
    )
    @POST
    @Path("/statistics")
    @Override
    public Uni<DescriptiveStatisticsDto> statistics(TsFunctionInputDto request) {
        return Uni.createFrom().item(Helpers.statistics(request));
    }

    @POST
    @Path("/pct")
    @Override
    public Uni<TsFunctionOutputDto> pct(PctInputDto request) {
        return Uni.createFrom().item(Helpers.pct(request));
    }

    @POST
    @Path("/delta")
    @Override
    public Uni<TsFunctionOutputDto> delta(DeltaInputDto request) {
        return Uni.createFrom().item(Helpers.delta(request));
    }

    @POST
    @Path("/aggregate")
    @Override
    public Uni<TsFunctionOutputDto> aggregate(AggregationInputDto request) {
        return Uni.createFrom().item(Helpers.aggregate(request));
    }

    @POST
    @Path("/hodrickPrescott")
    @Override
    public Uni<HodrickPrescottOutputDto> hodrickPrescott(HodrickPrescottInputDto request) {
        return Uni.createFrom().item(Helpers.hodrickPrescott(request));
    }

    @RequestBody(
            content = @Content(
                    examples = @ExampleObject(
                            name = "Example 1",
                            value = """
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
                                    """
                    )
            )
    )
    @POST
    @Path("/buildTsData")
    @Override
    public Uni<TsFunctionOutputDto> buildTsData(BuildTsDataInputDto request) {
        return Uni.createFrom().item(Helpers.buildTsData(request));
    }

    @Override
    public Multi<TsFunctionOutputDto> normalizeStream(Multi<TsFunctionInputDto> request) {
        return request.onItem().transform(Helpers::normalize);
    }

    @Override
    public Multi<DescriptiveStatisticsDto> statisticsStream(Multi<TsFunctionInputDto> request) {
        return request.onItem().transform(Helpers::statistics);
    }

    @Override
    public Multi<TsFunctionOutputDto> pctStream(Multi<PctInputDto> request) {
        return request.onItem().transform(Helpers::pct);
    }

    @Override
    public Multi<TsFunctionOutputDto> deltaStream(Multi<DeltaInputDto> request) {
        return request.onItem().transform(Helpers::delta);
    }

    @Override
    public Multi<TsFunctionOutputDto> aggregateStream(Multi<AggregationInputDto> request) {
        return request.onItem().transform(Helpers::aggregate);
    }

    @Override
    public Multi<HodrickPrescottOutputDto> hodrickPrescottStream(Multi<HodrickPrescottInputDto> request) {
        return request.onItem().transform(Helpers::hodrickPrescott);
    }

    @Override
    public Multi<TsFunctionOutputDto> buildTsDataStream(Multi<BuildTsDataInputDto> request) {
        return request.onItem().transform(Helpers::buildTsData);
    }

    @RequestBody(
            content = @Content(
                    examples = @ExampleObject(
                            name = "Example 1",
                            value = """
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
                                    """
                    )
            )
    )
    @POST
    @Path("/buildTsDataTable")
    @Override
    public Uni<BuildTsDataTableOutputDto> buildTsDataTable(BuildTsDataTableInputDto request) {
        return Uni.createFrom().item(Helpers.buildTsDataTable(request));
    }

    @Override
    public Uni<TemporalDisaggregationResultsDto> processTemporalDisaggregation(TemporalDisaggregationRequestDto request) {
        return Uni.createFrom().item(Helpers.processTemporalDisaggregation(request));
    }

    @Override
    public Uni<MatrixDto> tramoForecast(TramoForecastRequestDto request) {
        return Uni.createFrom().item(Helpers.tramoForecast(request));
    }
}
