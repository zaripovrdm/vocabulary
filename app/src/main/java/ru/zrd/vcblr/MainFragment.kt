package ru.zrd.vcblr

import android.content.ActivityNotFoundException
import android.content.SharedPreferences
import android.net.Uri
import android.os.Bundle
import android.view.*
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.coroutineScope
import androidx.lifecycle.lifecycleScope
import androidx.navigation.findNavController
import androidx.preference.PreferenceManager
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import ru.zrd.vcblr.csv.PhrasalVerbAdapter
import ru.zrd.vcblr.csv.VerbAdapter
import ru.zrd.vcblr.csv.VocabularyEntryAdapter
import ru.zrd.vcblr.databinding.FragmentMainBinding
import ru.zrd.vcblr.db.Db
import ru.zrd.vcblr.db.VocabularyEntry
import ru.zrd.vcblr.model.VocabularyModel
import ru.zrd.vcblr.model.VocabularyModelFactory
import ru.zrd.vcblr.ui.SelectCsvFileContract

class MainFragment : Fragment() {

    private val viewModel: VocabularyModel by viewModels {
        VocabularyModelFactory(requireContext())
    }

    private var binding: FragmentMainBinding? = null // TODO lateinit

    private lateinit var verbLauncher: ActivityResultLauncher<Unit>
    private lateinit var phrasalVerbLauncher: ActivityResultLauncher<Unit>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)

        verbLauncher = createLauncher(VerbAdapter(requireActivity().application.contentResolver))
        phrasalVerbLauncher = createLauncher(PhrasalVerbAdapter(requireActivity().application.contentResolver))
        // TODO add adapters
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        binding = FragmentMainBinding.inflate(inflater, container, false)
        return binding!!.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding?.apply {
            viewModel = this@MainFragment.viewModel
            lifecycleOwner = viewLifecycleOwner
        }
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.menu, menu)
    }

    override fun onOptionsItemSelected(item: MenuItem) = try {
        when (item.itemId) {
            R.id.action_add_verb -> {
                verbLauncher.launch(Unit)
                true
            }
            R.id.action_add_pverb -> {
                phrasalVerbLauncher.launch(Unit)
                true
            }
            R.id.action_settings -> {
                requireView().findNavController().navigate(MainFragmentDirections.actionMainFragmentToSettingsFragment())
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    } catch (ex: ActivityNotFoundException) {
        Toast.makeText(context, "It seems you need File Manager to be installed", Toast.LENGTH_LONG).show()
        true
    } catch (ex: Exception) {
        Toast.makeText(context, "An error has occured. ${ex::class.qualifiedName}", Toast.LENGTH_LONG).show()
        true
    }

    override fun onDestroyView() {
        super.onDestroyView()
        binding = null
    }

    private fun createLauncher(adapter: VocabularyEntryAdapter) = registerForActivityResult(SelectCsvFileContract()) { uri ->
        if (uri == null) {
            Toast.makeText(context, "File URI is null", Toast.LENGTH_LONG).show()
        } else {
            lifecycleScope.launch(Dispatchers.IO) {
                val entries = adapter.entries(uri)
                viewModel.addEntries(entries)
            }
        }
    }

//    override fun onResume() {
//        super.onResume()
//        // TODO preferences.registerOnSharedPreferenceChangeListener
//    }
//
//    override fun onPause() {
//        super.onPause()
//        // TODO preferences.unregisterOnSharedPreferenceChangeListener
//    }
}