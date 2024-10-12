package org.app.model.entity;

import org.app.model.Contact;
import org.app.model.OtherExperiencies;
import org.app.model.SpokenLanguages;
import org.app.model.UserExperiences;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import static org.app.utils.Commons.isEmpty;
import static org.app.utils.Commons.isTheSame;

@Document(collection = "user")
public class User {
    @Id
    private String _id; // MongoDB's internal identifier field

    private int id; // Front-end ID
    private String name;
    private String profession;
    private String resume;
    private List<String> competence;
    private List<String> social;
    private List<String> grade;
    private String ability;
    private String avatarImg;
    private String realImg;
    private Contact contact;
    private boolean actived = false;
    private String activationCode;
    private String deleteToken;
    private List<UserExperiences> userExperiences = new ArrayList<UserExperiences>();
    private OtherExperiencies otherExperiencies;
    private String language;
    List<SpokenLanguages> spokenLanguages;
    LocalDateTime lastUpdated;
    LocalDateTime createdDate;
    ArrayList<String> otherInfos;

    // Constructors
    public User() {
    }

    public User(String _id, int id, String name, String profession, String resume, List<String> competence,
                List<String> social, List<String> grade, String ability, String avatarImg, String realImg,
                Contact contact, String deleteToken, List<UserExperiences> experiences, String language,
                List<SpokenLanguages> spokenLanguages) {
        this._id = _id;
        this.id = id;
        this.name = name;
        this.profession = profession;
        this.resume = resume;
        this.competence = competence;
        this.social = social;
        this.grade = grade;
        this.ability = ability;
        this.avatarImg = avatarImg;
        this.realImg = realImg;
        this.contact = contact;
        this.deleteToken = deleteToken;
        this.userExperiences = experiences;
        this.language = language;
        this.spokenLanguages = spokenLanguages;
        ZoneId timeZone = ZoneId.of("America/Sao_Paulo");
        this.createdDate = LocalDateTime.now(timeZone);
    }

    // Getters and Setters
    public String get_id() {
        return _id;
    }

    public void set_id(String _id) {
        this._id = _id;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public boolean isActived() {
        return actived;
    }

    public String getActivationCode() {
        return activationCode;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getProfession() {
        return profession;
    }

    public void setProfession(String profession) {
        this.profession = profession;
    }

    public String getResume() {
        return resume;
    }

    public void setResume(String resume) {
        this.resume = resume;
    }

    public List<String> getCompetence() {
        return competence;
    }

    public void setCompetence(List<String> competence) {
        this.competence = competence;
    }

    public List<String> getSocial() {
        return social;
    }

    public void setSocial(List<String> social) {
        this.social = social;
    }

    public List<String> getGrade() {
        return grade;
    }

    public void setGrade(List<String> grade) {
        this.grade = grade;
    }

    public String getAbility() {
        return ability;
    }

    public void setAbility(String ability) {
        this.ability = ability;
    }

    public String getAvatarImg() {
        return avatarImg;
    }

    public void setAvatarImg(String avatarImg) {
        this.avatarImg = avatarImg;
    }

    public String getRealImg() {
        return realImg;
    }

    public void setRealImg(String realImg) {
        this.realImg = realImg;
    }

    public Contact getContact() {
        return contact;
    }

    public void setContact(Contact contact) {
        this.contact = contact;
    }

    public void setActivationCode(String activationCode) {
        this.activationCode = activationCode;
    }

    public void setActived(boolean actived) {
        this.actived = actived;
    }

    public void setDeteToken(String token) {
        this.deleteToken = token;
    }

    public String getLanguage() {
        return language;
    }

    public void setLanguage(String language) {
        this.language = language;
    }

    public List<SpokenLanguages> getSpokenLanguages() {
        return spokenLanguages;
    }

    public void setSpokenLanguages(List<SpokenLanguages> spokenLanguages) {
        this.spokenLanguages = spokenLanguages;
    }

    public OtherExperiencies getOtherExperiencies() {
        return otherExperiencies;
    }

    public void setOtherExperiencies(OtherExperiencies otherExperiencies) {
        this.otherExperiencies = otherExperiencies;
    }

    public LocalDateTime getLastUpdated() {
        return lastUpdated;
    }

    public ArrayList<String> getOtherInfos() {
        return otherInfos;
    }

    public void setOtherInfos(ArrayList<String> otherInfos) {
        this.otherInfos = otherInfos;
    }

    public void setLastUpdatedToNow() {
        ZoneId timeZone = ZoneId.of("America/Sao_Paulo");
        this.lastUpdated = LocalDateTime.now(timeZone);
    }

    public boolean equalsDeleteToken(String token) {
        if (isEmpty(token)) {
            return false;
        }
        return isTheSame(this.deleteToken, token);
    }

    // Equals and HashCode
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        User user = (User) o;
        return id == user.id && Objects.equals(_id, user._id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(_id, id);
    }

    public String getDeleteToken() {
        return deleteToken;
    }

    public void setDeleteToken(String deleteToken) {
        this.deleteToken = deleteToken;
    }

    public List<UserExperiences> getUserExperiences() {
        return userExperiences;
    }

    public void setUserExperiences(List<UserExperiences> userExperiences) {
        this.userExperiences = userExperiences;
    }

    @Override
    public String toString() {
        return "User{" +
                "_id='" + _id + '\'' +
                ", id=" + id +
                ", name='" + name + '\'' +
                ", profession='" + profession + '\'' +
                ", resume='" + resume + '\'' +
                ", competence=" + competence +
                ", social=" + social +
                ", grade=" + grade +
                ", ability='" + ability + '\'' +
                ", avatarImg='" + avatarImg + '\'' +
                ", realImg='" + realImg + '\'' +
                ", contact=" + contact +
                ", actived=" + actived +
                ", activationCode='" + activationCode + '\'' +
                ", deleteToken='" + deleteToken + '\'' +
                ", experiences=" + userExperiences +
                '}';
    }
}
