package studios.ingot.databaseapi.interfaces;

import java.util.List;

public interface IDatabaseHandler<T> {

    <K> void insertModel(K... query);
    <K> T getAsyncModel(K... query);
    <K> List<T> getAsyncModels(K... query);
    <K> void updateModel(K... query);
    <K> void deleteModel(K... query);

}
