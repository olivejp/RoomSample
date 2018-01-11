/*
 * Copyright 2017, The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package firebaseauthcom.example.orlanth23.roomsample.activity.adapter;

import android.databinding.DataBindingUtil;
import android.support.v7.util.DiffUtil;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import java.util.List;
import java.util.Objects;

import firebaseauthcom.example.orlanth23.roomsample.R;
import firebaseauthcom.example.orlanth23.roomsample.database.entity.ColisEntity;
import firebaseauthcom.example.orlanth23.roomsample.databinding.AdapterColisBinding;

public class ColisAdapter extends RecyclerView.Adapter<ColisAdapter.viewHolderColisAdapter> {

    List<? extends ColisEntity> colisEntities;

    public ColisAdapter() {
    }

    public void setProductList(final List<? extends ColisEntity> colisEntities1) {
        if (colisEntities == null) {
            colisEntities = colisEntities1;
            notifyItemRangeInserted(0, colisEntities1.size());
        } else {
            DiffUtil.DiffResult result = DiffUtil.calculateDiff(new DiffUtil.Callback() {
                @Override
                public int getOldListSize() {
                    return colisEntities.size();
                }

                @Override
                public int getNewListSize() {
                    return colisEntities1.size();
                }

                @Override
                public boolean areItemsTheSame(int oldItemPosition, int newItemPosition) {
                    return colisEntities.get(oldItemPosition).getIdColis().equals(colisEntities1.get(newItemPosition).getIdColis());
                }

                @Override
                public boolean areContentsTheSame(int oldItemPosition, int newItemPosition) {
                    ColisEntity newColis = colisEntities1.get(newItemPosition);
                    ColisEntity oldColis = colisEntities.get(oldItemPosition);
                    return Objects.equals(newColis.getIdColis(), oldColis.getIdColis())
                            && Objects.equals(newColis.getDescription(), oldColis.getDescription())
                            && Objects.equals(newColis.getSlug(), oldColis.getSlug());
                }
            });
            colisEntities = colisEntities1;
            result.dispatchUpdatesTo(this);
        }
    }

    @Override
    public viewHolderColisAdapter onCreateViewHolder(ViewGroup parent, int viewType) {
        AdapterColisBinding binding = DataBindingUtil
                .inflate(LayoutInflater.from(parent.getContext()), R.layout.adapter_colis, parent, false);
        return new viewHolderColisAdapter(binding);
    }

    @Override
    public void onBindViewHolder(viewHolderColisAdapter holder, int position) {
        holder.binding.setColis(colisEntities.get(position));
        holder.binding.executePendingBindings();
    }

    @Override
    public int getItemCount() {
        return colisEntities == null ? 0 : colisEntities.size();
    }

    public class viewHolderColisAdapter extends RecyclerView.ViewHolder {

        final AdapterColisBinding binding;

        public viewHolderColisAdapter(AdapterColisBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }
    }
}
