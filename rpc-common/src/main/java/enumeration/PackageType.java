package enumeration;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * @author submerge
 */
@AllArgsConstructor
@Getter
public enum PackageType {

    REQUEST_PACK(0),
    RESPONSE_PACK(1);

    private final int code;

}
