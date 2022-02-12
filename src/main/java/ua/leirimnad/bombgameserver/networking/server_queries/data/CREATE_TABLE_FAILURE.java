package ua.leirimnad.bombgameserver.networking.server_queries.data;

//TODO где использовать этот класс
// может ли игрок зайти дваэды в одну и ту же игру
public class CREATE_TABLE_FAILURE implements ServerQueryData {
    String description;

    public CREATE_TABLE_FAILURE(String description){
        this.description = description;
    }
}
