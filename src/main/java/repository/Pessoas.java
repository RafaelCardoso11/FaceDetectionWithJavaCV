package repository;


import java.util.Arrays;
import java.util.Objects;

interface  Pessoa {
   int  getId();
   String getNome();
}


class PessoaImpl implements Pessoa {
    private int id;
    private String nome;

    public PessoaImpl(int id, String nome) {
        this.id = id;
        this.nome = nome;
    }

    @Override
    public int getId() {
        return id;
    }

    @Override
    public String getNome() {
        return nome;
    }
}
public class Pessoas {

    static Pessoa[] pessoas = {
            new PessoaImpl(1, "rafael"),
            new PessoaImpl(2, "rebeca")
    };
   static public int  getIdByNome(String nome){
       return Arrays.stream(pessoas).filter(pessoa -> Objects.equals(pessoa.getNome(), nome)).findFirst().orElse(null).getId();
   }

   static public String  getNomeById(int id){
       return Arrays.stream(pessoas).filter(pessoa -> Objects.equals(pessoa.getId(), id)).findFirst().orElse(null).getNome();
   }
}
