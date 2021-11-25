package io.agora.agoravoice.im.viewmodels;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.arch.core.util.Function;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MediatorLiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Transformations;

import io.agora.agoravoice.business.log.Logging;
import io.agora.agoravoice.im.body.User;
import io.agora.agoravoice.im.reponsitories.EmClientRepository;
import io.agora.agoravoice.im.reponsitories.ErrorCode;
import io.agora.agoravoice.im.reponsitories.NetworkOnlyResource;
import io.agora.agoravoice.im.reponsitories.Resource;
import io.agora.agoravoice.im.reponsitories.ResultCallBack;
import io.agora.agoravoice.im.reponsitories.Status;


public class LoginViewModel extends AndroidViewModel {
    private EmClientRepository repository;
    private MediatorLiveData<Resource<User>> loginObservable;

    public LoginViewModel(@NonNull Application application) {
        super(application);
        repository = new EmClientRepository();
        loginObservable = new MediatorLiveData<>();
    }

    public MediatorLiveData<Resource<User>> getLoginObservable() {
        Logging.i("getLoginObservable   ");
        return loginObservable;
    }

    /**
     * 登录IM
     * @param user
     * @return
     */
    public LiveData<Resource<User>> loginIM(User user){
     return repository.loginIm(user);
    }



    /**
     * 登录环信
     */
    public void login(User user) {
//        //先随机获取一个用户
//        User user = UserRepository.getInstance().getCurrentUser();
        Logging.i("createImUser 2  login  " +user.getNickname());
        //先登录，如果没有登录成功则进行注册，如果注册成功再进行登录
        LiveData<Resource<User>> livedata = Transformations.switchMap(repository.Login(user), new Function<Resource<User>, LiveData<Resource<User>>>() {
            @Override
            public LiveData<Resource<User>> apply(Resource<User> input) {
                if (input.status == Status.SUCCESS) {
                    return new MutableLiveData<>(input);
                } else if (input.status == Status.ERROR) {
                    int errorCode = input.errorCode;
                    if (errorCode == ErrorCode.USER_NOT_FOUND) {
                        return Transformations.switchMap(repository.register(user), input2 -> {
                            if (input2.status == Status.SUCCESS) {
                                return repository.Login(input2.data);
                            } else if (input2.status == Status.ERROR) {
                                return new MutableLiveData<>(Resource.error(input2.errorCode, input2.getMessage(), null));
                            } else {
                                return new MutableLiveData<>(Resource.loading(null));
                            }
                        });
                    } else {
                        return new MutableLiveData<>(Resource.error(input.errorCode, input.getMessage(), null));
                    }
                } else {
                    return new MutableLiveData<>(Resource.loading(null));
                }
            }
        });
        loginObservable.addSource(livedata, response -> loginObservable.postValue(response));
    }
}
