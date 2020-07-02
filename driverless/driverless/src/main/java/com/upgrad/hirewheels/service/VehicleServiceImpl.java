package com.upgrad.hirewheels.service;

import com.upgrad.hirewheels.exceptions.APIException;
import com.upgrad.hirewheels.responsemodel.VehicleDetailResponse;
import com.upgrad.hirewheels.entities.*;
import com.upgrad.hirewheels.dao.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;


@Service
public class VehicleServiceImpl implements VehicleService {

    @Autowired
    VehicleDAO vehicleDAO;

    @Autowired
    VehicleCategoryDAO vehicleCategoryDAO;

    @Autowired
    RequestStatusDAO requestStatusDAO;

    @Autowired
    AdminRequestDAO adminRequestDAO;

    @Autowired
    BookingDAO bookingDAO;

    @Autowired
    LocationDAO locationDAO;


    @Autowired
    FuelTypeDAO fuelTypeDAO;

    @Autowired
    CityDAO cityDAO;

    @Autowired
    UserDAO userDAO;


    /**
     * Returns all the available vehicle in the requested Category for booking with respect to Date, Location and Availability.
     * @param categoryName
     * @param pickUpDate
     * @param dropDate
     * @param locationId
     * @return
     */

    public List<VehicleDetailResponse> getAvailableVehicles(String categoryName, Date pickUpDate,Date dropDate, int locationId) {
        List<Vehicle> returnedVehicleList = new ArrayList<>();
        if(vehicleCategoryDAO.findByVehicleCategoryName(categoryName) != null){
            vehicleCategoryDAO.findByVehicleCategoryName(categoryName).getVehicleSubCategoriesList().forEach(a-> a.getVehicleList().forEach(b-> {
                if (b.getLocationWithVehicle().getLocationId() == locationId) {
                    returnedVehicleList.add(b);
                }
            }));
        } else {
            throw new APIException("Invalid Vehicle Category Name");
        }

        List<Integer> bookedVehicles = new ArrayList<>();
        bookingDAO.findByPickUpDateGreaterThanEqualAndDropOffDateLessThanEqual(pickUpDate, dropDate).stream().forEach(a-> {bookedVehicles.add(a.getVehicleWithBooking().getVehicleId());});
        List<Integer> approvedVehicles = requestStatusDAO.findById(302).get().getAdminRequestList().stream().filter(a -> a.getActivity().getActivityId() != 204).map(AdminRequest::getVehicle).map(Vehicle::getVehicleId).collect(Collectors.toList());
        List<VehicleDetailResponse> mapVehicle = new ArrayList<>();
        for (Vehicle v : returnedVehicleList) {
            if (approvedVehicles.contains(v.getVehicleId())) {
                if(!bookedVehicles.contains(v.getVehicleId())){
                    VehicleDetailResponse y = new VehicleDetailResponse();
                    y.setVehicleId(v.getVehicleId());
                    y.setVehicleModel(v.getVehicleModel());
                    y.setVehicleOwnerId(v.getUser().getUserId());
                    y.setVehicleOwnerName(v.getUser().getFirstName());
                    y.setVehicleNumber(v.getVehicleNumber());
                    y.setColor(v.getColor());
                    y.setCostPerHour(v.getVehicleSubCategory().getPricePerHour());
                    y.setFuelType(v.getFuelType().getFuelType());
                    y.setLocationId(v.getLocationWithVehicle().getLocationId());
                    y.setCarImageUrl(v.getCarImageUrl());
                    mapVehicle.add(y);
                }
            }
        }
       return mapVehicle;
    }

    /**
     * Returns all the vehicle registered by user.
     * @param userId
     * @return
     */

    public List<VehicleDetailResponse> getAllVehicleByUserId(int userId) {
        List<VehicleDetailResponse> mapVehicle = new ArrayList<>();
        List<Vehicle> returnedVehicleList;
        if (userId != 1){
            returnedVehicleList = userDAO.findById(userId).get().getVehiclesList();
        } else {
            returnedVehicleList = vehicleDAO.findAll();
        }
        for (Vehicle v : returnedVehicleList) {
                VehicleDetailResponse y = new VehicleDetailResponse();
                y.setVehicleId(v.getVehicleId());
                y.setVehicleModel(v.getVehicleModel());
                y.setVehicleOwnerId(v.getUser().getUserId());
                y.setVehicleNumber(v.getVehicleNumber());
                y.setVehicleOwnerName(v.getUser().getFirstName());
                y.setColor(v.getColor());
                y.setCostPerHour(v.getVehicleSubCategory().getPricePerHour());
                y.setFuelType(v.getFuelType().getFuelType());
                y.setLocationId(v.getLocationWithVehicle().getLocationId());
                y.setCarImageUrl(v.getCarImageUrl());
                y.setActivityId(v.getAdminRequest().getActivity().getActivityId());
                y.setRequestStatusId(v.getAdminRequest().getRequestStatus().getRequestStatusId());
                mapVehicle.add(y);
        }
        return mapVehicle;
    }

}

