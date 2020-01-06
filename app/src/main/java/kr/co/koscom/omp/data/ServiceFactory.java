package kr.co.koscom.omp.data;

import kr.co.koscom.omp.BuildConfig;
import kr.co.koscom.omp.data.service.AlarmService;
import kr.co.koscom.omp.data.service.ChatService;
import kr.co.koscom.omp.data.service.CodeService;
import kr.co.koscom.omp.data.service.LoginService;
import kr.co.koscom.omp.data.service.NegoService;
import kr.co.koscom.omp.data.service.OrderService;
import kr.co.koscom.omp.data.service.TestService;
import kr.co.koscom.omp.data.service.UpdateService;

public class ServiceFactory {

    private ServiceFactory() {}

    public static final String BASE_URL = BuildConfig.SERVER_URL;

    public static final String TEST_URL = "http://jsonplaceholder.typicode.com/";

    public static TestService getTestService() {

        return RetrofitClient.getClient(TEST_URL).create(TestService.class);
    }

    public static ChatService getChatService() {

        return RetrofitClient.getClient(BASE_URL).create(ChatService.class);
    }

    public static LoginService getLoginService() {

        return RetrofitClient.getClient(BASE_URL).create(LoginService.class);
    }

    public static OrderService getOrderService() {

        return RetrofitClient.getClient(BASE_URL).create(OrderService.class);
    }

    public static AlarmService getAlarmService() {

        return RetrofitClient.getClient(BASE_URL).create(AlarmService.class);
    }

    public static CodeService getCodeService() {

        return RetrofitClient.getClient(BASE_URL).create(CodeService.class);
    }

    public static NegoService getNegoService() {

        return RetrofitClient.getClient(BASE_URL).create(NegoService.class);
    }

    public static UpdateService getUpdateService() {

        return RetrofitClient.getClient(BASE_URL).create(UpdateService.class);
    }
}
