package de.dertyp7214.rboardthememanager.fragments

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.RecyclerView
import de.dertyp7214.rboardcomponents.utils.asyncInto
import de.dertyp7214.rboardcomponents.utils.doAsyncCallback
import de.dertyp7214.rboardthememanager.R
import de.dertyp7214.rboardthememanager.adapter.SoundPackAdapter
import de.dertyp7214.rboardthememanager.components.LayoutManager
import de.dertyp7214.rboardthememanager.core.applyTransitions
import de.dertyp7214.rboardthememanager.core.applyTransitionsViewCreated
import de.dertyp7214.rboardthememanager.core.get
import de.dertyp7214.rboardthememanager.data.SoundPack
import de.dertyp7214.rboardthememanager.utils.SoundHelper
import de.dertyp7214.rboardthememanager.viewmodels.MainViewModel

class SoundsFragment : Fragment() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        applyTransitions()
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_sounds, container, false)
    }

    @SuppressLint("NotifyDataSetChanged")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        applyTransitionsViewCreated()

        val recyclerView = view.findViewById<RecyclerView>(R.id.recyclerView)

        val original = arrayListOf<SoundPack>()
        val soundList = ArrayList(original)

        val adapter = SoundPackAdapter(requireContext(), soundList, requireActivity())

        recyclerView.setHasFixedSize(true)
        recyclerView.layoutManager = LayoutManager(requireContext())
        recyclerView.adapter = adapter

        val mainViewModel = requireActivity()[MainViewModel::class.java]

        mainViewModel.observeFilter(this) { filter ->
            synchronized(soundList) {
                soundList.clear()
                soundList.addAll(original.filter {
                    it.author.contains(filter, true)
                            || it.title.contains(filter, true)
                            || filter.isEmpty()
                })
                adapter.notifyDataSetChanged()
            }
        }

        mainViewModel.soundsObserve(this) { sounds ->
            if (sounds.isEmpty()) SoundHelper::loadSoundPacks asyncInto mainViewModel::setSounds
            else
                doAsyncCallback<Any?>({
                    synchronized(original) {
                        synchronized(soundList) {
                            original.clear()
                            soundList.clear()
                            original.addAll(sounds)
                            soundList.addAll(sounds)
                            it(null)
                        }
                    }
                }) {
                    adapter.notifyDataSetChanged()
                }
        }

        SoundHelper::loadSoundPacks asyncInto mainViewModel::setSounds
    }
}