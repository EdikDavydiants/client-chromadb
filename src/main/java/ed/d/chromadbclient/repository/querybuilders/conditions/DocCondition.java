package ed.d.chromadbclient.repository.querybuilders.conditions;

public class DocCondition extends Condition {
    private final String substring;

    protected DocCondition(String contains, String substring) {
        super(contains);
        this.substring = substring;
    }

    @Override
    public Object content() {
        return substring;
    }
}
