package co.edu.eci.blueprints.api.filters;
import co.edu.eci.blueprints.api.model.Blueprint;
public interface BlueprintsFilter {
    Blueprint apply(Blueprint bp);
}
