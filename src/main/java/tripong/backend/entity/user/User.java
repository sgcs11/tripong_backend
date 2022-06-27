package tripong.backend.entity.user;

import lombok.*;
import tripong.backend.dto.account.FirstExtraInfoPutRequestDto;
import tripong.backend.entity.base.BaseTimeEntity;

import javax.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDate;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
public class User extends BaseTimeEntity {

    @Id @GeneratedValue
    private Long id;

    private String loginId; //로그인ID

    private String password;

    private String name;

    private String nickName;

    private String email;

    private String picture;

    private LocalDate birthDate;

    @Enumerated(EnumType.STRING)
    private GenderType gender;

    private String introduction;

    private String phoneNumber;

    @Enumerated(EnumType.STRING)
    private JoinType joinMethod;

    private Integer authentication;

    @Enumerated(EnumType.STRING)
    private RoleType role;

    private String city;
    private String district;

    private BigDecimal latitude;

    private BigDecimal longitude;

    ////-----편의 메소드-----
    //추가정보입력
    public void putExtraInfo(FirstExtraInfoPutRequestDto dto){
        this.name = dto.getName();
        this.gender = dto.getGender();
        this.birthDate = dto.getBirthDate();
        this.city = dto.getCity();
        this.district = dto.getDistrict();
        this.role = RoleType.User;
    }

    //init 용
    @Builder
    public User(String loginId, String password, String name, String nickName,
                String email, LocalDate birthDate, GenderType gender, JoinType joinMethod,
                 int authentication, RoleType role, String city, String district) {
        this.loginId=loginId;
        this.password=password;
        this.name=name;
        this.nickName=nickName;
        this.email=email;
        this.birthDate=birthDate;
        this.gender=gender;
        this.joinMethod=joinMethod;
        this.authentication=authentication;
        this.role=role;
        this.city=city;
        this.district=district;
    }


}
