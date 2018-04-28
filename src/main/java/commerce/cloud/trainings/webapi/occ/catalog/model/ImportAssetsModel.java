package commerce.cloud.trainings.webapi.occ.catalog.model;

public class ImportAssetsModel {

    public static final String TOTAL_ARG = "total";
    public static final String NEW_SUCCESS_COUNT_ARG = "newSuccessCount";
    public static final String NEW_ERROR_COUNT_ARG = "newErrorCount";
    public static final String MODIFIED_SUCCESS_COUNT_ARG = "modifiedSuccessCount";
    public static final String MODIFIED_ERROR_COUNT_ARG = "modifiedErrorCount";
    public static final String UNCHANGED_COUNT_ARG = "unchangedCount";

    private int total;
    private int newSuccessCount;
    private int newErrorCount;
    private int modifiedSuccessCount;
    private int modifiedErrorCount;
    private int unchangedCount;

    public ImportAssetsModel() {}

    public ImportAssetsModel(int total, int newSuccessCount, int newErrorCount,
                             int modifiedSuccessCount, int modifiedErrorCount, int unchangedCount) {
        this.total = total;
        this.newSuccessCount = newSuccessCount;
        this.newErrorCount = newErrorCount;
        this.modifiedSuccessCount = modifiedSuccessCount;
        this.modifiedErrorCount = modifiedErrorCount;
        this.unchangedCount = unchangedCount;
    }

    public int getTotal() {
        return total;
    }

    public void setTotal(int total) {
        this.total = total;
    }

    public int getNewSuccessCount() {
        return newSuccessCount;
    }

    public void setNewSuccessCount(int newSuccessCount) {
        this.newSuccessCount = newSuccessCount;
    }

    public int getNewErrorCount() {
        return newErrorCount;
    }

    public void setNewErrorCount(int newErrorCount) {
        this.newErrorCount = newErrorCount;
    }

    public int getModifiedSuccessCount() {
        return modifiedSuccessCount;
    }

    public void setModifiedSuccessCount(int modifiedSuccessCount) {
        this.modifiedSuccessCount = modifiedSuccessCount;
    }

    public int getModifiedErrorCount() {
        return modifiedErrorCount;
    }

    public void setModifiedErrorCount(int modifiedErrorCount) {
        this.modifiedErrorCount = modifiedErrorCount;
    }

    public int getUnchangedCount() {
        return unchangedCount;
    }

    public void setUnchangedCount(int unchangedCount) {
        this.unchangedCount = unchangedCount;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        ImportAssetsModel that = (ImportAssetsModel) o;

        if (total != that.total) return false;
        if (newSuccessCount != that.newSuccessCount) return false;
        if (newErrorCount != that.newErrorCount) return false;
        if (modifiedSuccessCount != that.modifiedSuccessCount) return false;
        if (modifiedErrorCount != that.modifiedErrorCount) return false;
        return unchangedCount == that.unchangedCount;
    }

    @Override
    public int hashCode() {
        int result = total;
        result = 31 * result + newSuccessCount;
        result = 31 * result + newErrorCount;
        result = 31 * result + modifiedSuccessCount;
        result = 31 * result + modifiedErrorCount;
        result = 31 * result + unchangedCount;
        return result;
    }

    @Override
    public String toString() {
        return "ImportAssetsModel{" +
                "total=" + total +
                ", newSuccessCount=" + newSuccessCount +
                ", newErrorCount=" + newErrorCount +
                ", modifiedSuccessCount=" + modifiedSuccessCount +
                ", modifiedErrorCount=" + modifiedErrorCount +
                ", unchangedCount=" + unchangedCount +
                '}';
    }
}
