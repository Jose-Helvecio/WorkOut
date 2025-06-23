import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.goshtflix.databinding.ItemExecucaoBinding
import com.example.goshtflix.model.Execucao
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class ExecucaoAdapter : RecyclerView.Adapter<ExecucaoAdapter.ViewHolder>() {

    private var lista = listOf<Execucao>()

    fun atualizarLista(novaLista: List<Execucao>) {
        lista = novaLista
        notifyDataSetChanged()
    }

    class ViewHolder(val binding: ItemExecucaoBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(execucao: Execucao) {
            binding.tvSerie.text = "Série ${execucao.serie}"
            binding.tvInfo.text = "${execucao.repeticoes} reps • ${execucao.peso} kg"
            binding.tvData.text = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
                .format(Date(execucao.data))
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = ItemExecucaoBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)
    }

    override fun getItemCount() = lista.size

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(lista[position])
    }
}
