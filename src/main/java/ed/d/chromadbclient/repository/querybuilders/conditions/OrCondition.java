package ed.d.chromadbclient.repository.querybuilders.conditions;

import java.util.List;

public class OrCondition extends Condition {
    private final List<Condition> conditions;

    protected OrCondition(List<Condition> conditions) {
        super("$or");
        this.conditions = conditions;
    }

    @Override
    public Object content() {
        return conditions.stream()
                .map(Condition::toMap)
                .toList();
    }
}
