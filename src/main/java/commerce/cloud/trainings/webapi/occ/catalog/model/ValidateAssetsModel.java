package commerce.cloud.trainings.webapi.occ.catalog.model;


/**
 * @author Nick Mandrik
 */


public class ValidateAssetsModel {

    public static final String TOTAL_ARG = "total";
    public static final String NEW_COUNT_ARG = "newCount";
    public static final String MODIFIED_COUNT_ARG = "modifiedCount";
    public static final String WARNING_COUNT_ARG = "warningCount";
    public static final String UNCHANGED_COUNT_ARG = "unchangedCount";
    public static final String ERROR_COUNT_ARG = "errorCount";
    public static final String TOKEN_ARG = "token";


    private int total;
    private int newCount;
    private int modifiedCount;
    private int warningCount;
    private int unchangedCount;
    private int errorCount;
    private String token;

    public ValidateAssetsModel() { }

    public ValidateAssetsModel(int total, int newCount, int modifiedCount, int warningCount,
                               int unchangedCount, int errorCount, String token) {
        this.total = total;
        this.newCount = newCount;
        this.modifiedCount = modifiedCount;
        this.warningCount = warningCount;
        this.unchangedCount = unchangedCount;
        this.errorCount = errorCount;
        this.token = token;
    }

    public int getTotal() {
        return total;
    }

    public void setTotal(int total) {
        this.total = total;
    }

    public int getNewCount() {
        return newCount;
    }

    public void setNewCount(int newCount) {
        this.newCount = newCount;
    }

    public int getModifiedCount() {
        return modifiedCount;
    }

    public void setModifiedCount(int modifiedCount) {
        this.modifiedCount = modifiedCount;
    }

    public int getWarningCount() {
        return warningCount;
    }

    public void setWarningCount(int warningCount) {
        this.warningCount = warningCount;
    }

    public int getUnchangedCount() {
        return unchangedCount;
    }

    public void setUnchangedCount(int unchangedCount) {
        this.unchangedCount = unchangedCount;
    }

    public int getErrorCount() {
        return errorCount;
    }

    public void setErrorCount(int errorCount) {
        this.errorCount = errorCount;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }


    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        ValidateAssetsModel that = (ValidateAssetsModel) o;

        if (total != that.total) return false;
        if (newCount != that.newCount) return false;
        if (modifiedCount != that.modifiedCount) return false;
        if (warningCount != that.warningCount) return false;
        if (unchangedCount != that.unchangedCount) return false;
        if (errorCount != that.errorCount) return false;
        return token != null ? token.equals(that.token) : that.token == null;
    }

    @Override
    public int hashCode() {
        int result = total;
        result = 31 * result + newCount;
        result = 31 * result + modifiedCount;
        result = 31 * result + warningCount;
        result = 31 * result + unchangedCount;
        result = 31 * result + errorCount;
        result = 31 * result + (token != null ? token.hashCode() : 0);
        return result;
    }

    @Override
    public String toString() {
        return "ValidateAssetsModel{" +
                "total=" + total +
                ", newCount=" + newCount +
                ", modifiedCount=" + modifiedCount +
                ", warningCount=" + warningCount +
                ", unchangedCount=" + unchangedCount +
                ", errorCount=" + errorCount +
                ", token='" + token + '\'' +
                '}';
    }
}