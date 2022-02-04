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

    private val db: Db by lazy {
        Db.instance(this.requireContext())
    }

    private val viewModel: VocabularyModel by viewModels {
        VocabularyModelFactory(db)
    }

    private var binding: FragmentMainBinding? = null // TODO lateinit

    private lateinit var verbLauncher: ActivityResultLauncher<Unit>
    private lateinit var phrasalVerbLauncher: ActivityResultLauncher<Unit>

    private lateinit var preferences: SharedPreferences

    private val preferenceListener = SharedPreferences.OnSharedPreferenceChangeListener { _, _ ->
        lifecycleScope.launch(Dispatchers.IO) {
            initScreen()
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)

        verbLauncher = createLauncher(VerbAdapter(requireActivity().application.contentResolver))
        phrasalVerbLauncher = createLauncher(PhrasalVerbAdapter(requireActivity().application.contentResolver))
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

        preferences = PreferenceManager.getDefaultSharedPreferences(requireContext())

        lifecycleScope.launch(Dispatchers.IO) {
            initScreen()
        }

        preferences.registerOnSharedPreferenceChangeListener(preferenceListener)
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

    override fun onResume() {
        super.onResume()
        preferences.registerOnSharedPreferenceChangeListener(preferenceListener)
    }

    override fun onPause() {
        super.onPause()
        preferences.unregisterOnSharedPreferenceChangeListener(preferenceListener)
    }


    private fun createLauncher(adapter: VocabularyEntryAdapter) = registerForActivityResult(SelectCsvFileContract()) { uri ->
        if (uri == null) {
            Toast.makeText(context, "File URI is null", Toast.LENGTH_LONG).show()
        } else {
            lifecycleScope.launch(Dispatchers.IO) {
                val entries = adapter.entries(uri)
                lifecycleScope.launch(Dispatchers.IO) {
                    viewModel.addEntries(entries)
                    initScreen()
                }
                //Toast.makeText(context, "${entries.size} entries have been successfully added", Toast.LENGTH_LONG).show()
            }
        }
    }

    private fun initScreen() {
        val activeTypes = mutableListOf<VocabularyEntry.Type>()
        if (preferences.getBoolean("verb", true)) {
            activeTypes.add(VocabularyEntry.Type.VERB)
        }
        if (preferences.getBoolean("pverb", true)) {
            activeTypes.add(VocabularyEntry.Type.PVERB)
        }
        if (preferences.getBoolean("noun", true)) {
            activeTypes.add(VocabularyEntry.Type.NOUN)
        }
        if (preferences.getBoolean("idiom", true)) {
            activeTypes.add(VocabularyEntry.Type.IDIOM)
        }

        viewModel.refresh(preferences.getBoolean("show_all", true), activeTypes)
    }
}