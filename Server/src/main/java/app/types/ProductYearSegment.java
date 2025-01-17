package app.types;

public class ProductYearSegment {
	public long productCode;
	public int segmentId;
	public int target;
	public int thresold;

	public ProductYearSegment(long productCode, int segmentId, int target, int thresold) {
		this.productCode = productCode;
		this.segmentId = segmentId;
		this.target = target;
		this.thresold = thresold;
	}
}