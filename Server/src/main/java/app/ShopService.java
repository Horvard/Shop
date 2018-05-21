package app;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.util.ArrayList;
import java.util.List;

public class ShopService {
    private static boolean accountBoolean  = false;
    ShopDao shopDao = new ShopDao();
    ObjectMapper mapper = new ObjectMapper() ;
    //------------------------------------------------------------------------------------------------------------------

    public String getAll() throws JsonProcessingException {
        ArrayList<Good> getAllGoodsArrayListServise = new ArrayList<Good>();
        getAllGoodsArrayListServise = shopDao.getAll();
        String returnList = mapper.writerWithDefaultPrettyPrinter().writeValueAsString(getAllGoodsArrayListServise) ;
        return returnList ;
    };

    //------------------------------------------------------------------------------------------------------------------

    public void addGoods(List<Good> goods) {
        ArrayList<Good> saveGoodsArray = new ArrayList<Good>();
        synchronized (saveGoodsArray) {
            saveGoodsArray = shopDao.getAll();
            for (Good newGoods : goods) {
                boolean newGoodBoolean = true;
                for (Good saveGood : saveGoodsArray) {
                    if (saveGood.name.equals(newGoods.name)) {
                        if (newGoods.count!=0){
                            saveGood.count = newGoods.count ;
                        } else {
                            saveGoodsArray.remove(saveGood) ;
                        }
                        saveGood.price = newGoods.price;
                        newGoodBoolean = false;

                    }
                }
                if (newGoodBoolean) {
                    saveGoodsArray.add(newGoods);
                }
            }
            shopDao.saveAll(saveGoodsArray);
        }
    }

    //------------------------------------------------------------------------------------------------------------------

    public void buyGoods(List<Good> arrayGoods) {
        int userBuyMoney = 0 ;
        Good findGood = null;
        //synchronized (findGood){
            for (Good buyGood : arrayGoods){
                findGood = shopDao.findBuyName(buyGood.name) ;
                if (findGood == null){
                    NullPointerException errorFindGood = new NullPointerException("Такого товара нет в магазине") ;
                    throw errorFindGood ;
                } else {
                    if (findGood.count==buyGood.count){
                    userBuyMoney = userBuyMoney + buyGood.count*findGood.price ;
                    shopDao.deleteByName(findGood.name);
                    }
                    if (findGood.count>buyGood.count){
                        findGood.count = findGood.count - buyGood.count ;
                        userBuyMoney = userBuyMoney + findGood.price*buyGood.count ;
                        shopDao.updateAll(findGood);
                    } else {
                        IllegalArgumentException errorUserBuyCount = new IllegalArgumentException("Такого колличества товара нет в магазине") ;
                        throw errorUserBuyCount ;
                    }
                }
          //  }
        }
    }
    //------------------------------------------------------------------------------------------------------------------
    public boolean getAccount (){
        return accountBoolean ;
    }

    //------------------------------------------------------------------------------------------------------------------

    public void addAccount(Account account){
        shopDao.addAccount(account);
    }

    //------------------------------------------------------------------------------------------------------------------

    public String postAccount (Account account){
        accountBoolean = false ;
        ArrayList<Account> accounts = shopDao.getAccount() ;
        Autorization autorization = new Autorization() ;
        String password = account.getPassword() ;
        password = autorization.Coding(password) ;
        String returName = "false";
        account.setPassword(password);
        for (Account findAccount: accounts){
            if (findAccount.getPassword().equals(account.getPassword())&&findAccount.getName().equals(account.getName())){
                if (account.getName().equals("root")){
                    returName = "root" ;
                    break;
                } else {
                    returName = "true" ;
                    break;
                }
            } else {
                returName = "false" ;
            }
        }
        return returName ;
    }
}